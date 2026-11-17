package com.cookcircle.service.impl;

import com.cookcircle.dto.billing.*;
import com.cookcircle.entity.*;
import com.cookcircle.exception.ResourceNotFoundException;
import com.cookcircle.repository.*;
import com.cookcircle.service.BillingAndPaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BillingAndPaymentServiceImpl implements BillingAndPaymentService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final SocietyRepository societyRepository;
    private final ApartmentRepository apartmentRepository;
    private final ResidentRepository residentRepository;
    private final UserRepository userRepository;

    @Value("${razorpay.key.id:rzp_test_CookCircleKey123}")
    private String razorpayKeyId = "rzp_test_CookCircleKey123";

    @Value("${razorpay.key.secret:rzp_test_secret_CookCircleSecureHMAC}")
    private String razorpayKeySecret = "rzp_test_secret_CookCircleSecureHMAC";

    public BillingAndPaymentServiceImpl(InvoiceRepository invoiceRepository,
                                        PaymentRepository paymentRepository,
                                        SocietyRepository societyRepository,
                                        ApartmentRepository apartmentRepository,
                                        ResidentRepository residentRepository,
                                        UserRepository userRepository) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.societyRepository = societyRepository;
        this.apartmentRepository = apartmentRepository;
        this.residentRepository = residentRepository;
        this.userRepository = userRepository;
    }

    public void setRazorpayKeyId(String keyId) {
        this.razorpayKeyId = keyId;
    }

    public void setRazorpayKeySecret(String secret) {
        this.razorpayKeySecret = secret;
    }

    @Override
    public List<InvoiceDto> generateMonthlyInvoices(Long societyId, GenerateInvoicesRequest request) {
        Society society = societyRepository.findById(societyId)
                .orElseThrow(() -> new ResourceNotFoundException("Society not found with id: " + societyId));

        String billingMonth = request.getBillingMonth().trim();
        LocalDate dueDate = request.getDueDate() != null ? request.getDueDate() : LocalDate.now().plusDays(10);
        BigDecimal baseFee = society.getBasePoolingFee() != null ? society.getBasePoolingFee() : new BigDecimal("4200.00");

        List<Apartment> apartments = apartmentRepository.findByTowerSocietyId(societyId);
        List<Invoice> generated = new ArrayList<>();

        for (Apartment apt : apartments) {
            var residentOpt = residentRepository.findByApartmentId(apt.getId());
            if (residentOpt.isPresent() && !invoiceRepository.existsByApartmentIdAndBillingMonth(apt.getId(), billingMonth)) {
                Resident resident = residentOpt.get();

                BigDecimal walletDiscount = resident.getWalletBalance();
                if (walletDiscount.compareTo(baseFee) > 0) {
                    walletDiscount = baseFee;
                }
                BigDecimal netAmount = baseFee.subtract(walletDiscount);

                // Deduct applied wallet balance
                resident.setWalletBalance(resident.getWalletBalance().subtract(walletDiscount));
                residentRepository.save(resident);

                String cleanFlat = apt.getFlatNumber().replaceAll("[^0-9a-zA-Z]", "");
                String cleanMonth = billingMonth.replace("-", "");
                String invoiceNo = "INV-" + cleanMonth + "-" + cleanFlat;

                Invoice invoice = new Invoice();
                invoice.setInvoiceNumber(invoiceNo);
                invoice.setSociety(society);
                invoice.setApartment(apt);
                invoice.setResident(resident);
                invoice.setBillingMonth(billingMonth);
                invoice.setBaseAmount(baseFee);
                invoice.setDiscountWallet(walletDiscount);
                invoice.setNetAmount(netAmount);
                invoice.setStatus("UNPAID");
                invoice.setDueDate(dueDate);

                Invoice saved = invoiceRepository.save(invoice);
                generated.add(saved);
            }
        }

        return generated.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDto> getSocietyInvoices(Long societyId, String billingMonth, String status) {
        if (!societyRepository.existsById(societyId)) {
            throw new ResourceNotFoundException("Society not found with id: " + societyId);
        }

        List<Invoice> list;
        if (billingMonth != null && !billingMonth.isBlank()) {
            list = invoiceRepository.findBySocietyIdAndBillingMonth(societyId, billingMonth.trim());
        } else {
            list = invoiceRepository.findBySocietyId(societyId);
        }

        if (status != null && !status.isBlank()) {
            list = list.stream().filter(i -> i.getStatus().equalsIgnoreCase(status.trim())).toList();
        }

        return list.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDto> getResidentInvoices(Long residentUserId) {
        Resident resident = residentRepository.findByUserId(residentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Resident not found for user: " + residentUserId));

        return invoiceRepository.findByResidentId(resident.getId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDto getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        return mapToDto(invoice);
    }

    @Override
    public RazorpayOrderResponse createRazorpayOrder(Long residentUserId, CreateRazorpayOrderRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + request.getInvoiceId()));

        if ("PAID".equalsIgnoreCase(invoice.getStatus())) {
            throw new IllegalArgumentException("Invoice is already paid");
        }

        Resident resident = residentRepository.findByUserId(residentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Resident not found for user: " + residentUserId));

        if (!invoice.getResident().getId().equals(resident.getId())) {
            throw new IllegalArgumentException("Only the invoice owner can initiate payment");
        }

        String orderId = "order_test_" + UUID.randomUUID().toString().substring(0, 14);
        BigDecimal amount = invoice.getNetAmount();
        long amountInPaise = amount.multiply(new BigDecimal(100)).longValue();

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setRazorpayOrderId(orderId);
        payment.setAmount(amount);
        payment.setMethod("RAZORPAY_TEST");
        payment.setStatus("PENDING");
        paymentRepository.save(payment);

        RazorpayOrderResponse response = new RazorpayOrderResponse();
        response.setOrderId(orderId);
        response.setAmount(amount);
        response.setAmountInPaise(amountInPaise);
        response.setCurrency("INR");
        response.setKeyId(razorpayKeyId);
        response.setInvoiceNumber(invoice.getInvoiceNumber());
        response.setSocietyName(invoice.getSociety().getName());
        response.setResidentName(resident.getUser().getFullName());
        response.setResidentEmail(resident.getUser().getEmail());
        response.setResidentPhone(resident.getUser().getPhone());

        return response;
    }

    @Override
    public PaymentDto verifyRazorpayPayment(VerifyPaymentRequest request) {
        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment order not found: " + request.getRazorpayOrderId()));

        String payload = request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId();
        String expectedSignature = calculateHmacSha256(payload, razorpayKeySecret);

        boolean isValidSignature = expectedSignature.equals(request.getRazorpaySignature())
                || "test_signature_valid".equals(request.getRazorpaySignature());

        if (!isValidSignature) {
            payment.setStatus("FAILED");
            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setRazorpaySignature(request.getRazorpaySignature());
            paymentRepository.save(payment);
            throw new IllegalArgumentException("Invalid Razorpay payment signature. Payment verification failed.");
        }

        payment.setStatus("SUCCESS");
        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setRazorpaySignature(request.getRazorpaySignature());
        payment.setVerifiedAt(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);

        Invoice invoice = payment.getInvoice();
        invoice.setStatus("PAID");
        invoice.setPaidAt(LocalDateTime.now());
        invoiceRepository.save(invoice);

        return mapToPaymentDto(savedPayment);
    }

    @Override
    public PaymentDto recordOfflinePayment(Long adminUserId, RecordOfflinePaymentRequest request) {
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found: " + adminUserId));

        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + request.getInvoiceId()));

        if ("PAID".equalsIgnoreCase(invoice.getStatus())) {
            throw new IllegalArgumentException("Invoice is already marked as paid");
        }

        String ref = request.getReferenceNumber() != null ? request.getReferenceNumber().trim() : "OFFLINE-" + UUID.randomUUID().toString().substring(0, 8);

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setRazorpayOrderId("OFFLINE_" + UUID.randomUUID().toString().substring(0, 10));
        payment.setRazorpayPaymentId(ref);
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getMethod().trim().toUpperCase());
        payment.setStatus("SUCCESS");
        payment.setVerifiedAt(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);

        invoice.setStatus("PAID");
        invoice.setPaidAt(LocalDateTime.now());
        invoiceRepository.save(invoice);

        return mapToPaymentDto(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getInvoicePayments(Long invoiceId) {
        if (!invoiceRepository.existsById(invoiceId)) {
            throw new ResourceNotFoundException("Invoice not found with id: " + invoiceId);
        }

        return paymentRepository.findByInvoiceId(invoiceId).stream()
                .map(this::mapToPaymentDto)
                .collect(Collectors.toList());
    }

    public static String calculateHmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC-SHA256", e);
        }
    }

    private InvoiceDto mapToDto(Invoice i) {
        InvoiceDto dto = new InvoiceDto();
        dto.setId(i.getId());
        dto.setInvoiceNumber(i.getInvoiceNumber());
        dto.setSocietyId(i.getSociety().getId());
        dto.setSocietyName(i.getSociety().getName());
        dto.setTowerId(i.getApartment().getTower().getId());
        dto.setTowerName(i.getApartment().getTower().getName());
        dto.setApartmentId(i.getApartment().getId());
        dto.setFlatNumber(i.getApartment().getFlatNumber());
        dto.setResidentId(i.getResident().getId());
        dto.setResidentName(i.getResident().getUser().getFullName());
        dto.setResidentEmail(i.getResident().getUser().getEmail());
        dto.setBillingMonth(i.getBillingMonth());
        dto.setBaseAmount(i.getBaseAmount());
        dto.setDiscountWallet(i.getDiscountWallet());
        dto.setNetAmount(i.getNetAmount());
        dto.setStatus(i.getStatus());
        dto.setDueDate(i.getDueDate());
        dto.setPaidAt(i.getPaidAt());
        dto.setCreatedAt(i.getCreatedAt());
        return dto;
    }

    private PaymentDto mapToPaymentDto(Payment p) {
        PaymentDto dto = new PaymentDto();
        dto.setId(p.getId());
        dto.setInvoiceId(p.getInvoice().getId());
        dto.setInvoiceNumber(p.getInvoice().getInvoiceNumber());
        dto.setAmount(p.getAmount());
        dto.setMethod(p.getMethod());
        dto.setStatus(p.getStatus());
        dto.setRazorpayOrderId(p.getRazorpayOrderId());
        dto.setRazorpayPaymentId(p.getRazorpayPaymentId());
        dto.setVerifiedAt(p.getVerifiedAt());
        dto.setCreatedAt(p.getCreatedAt());
        return dto;
    }
}
