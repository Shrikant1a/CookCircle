package com.cookcircle.service;

import com.cookcircle.dto.billing.*;
import com.cookcircle.entity.*;
import com.cookcircle.repository.*;
import com.cookcircle.service.impl.BillingAndPaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class BillingAndPaymentTests {

    private final Map<Long, Invoice> invoiceTable = new HashMap<>();
    private final Map<Long, Payment> paymentTable = new HashMap<>();
    private final Map<Long, Society> societyTable = new HashMap<>();
    private final Map<Long, Tower> towerTable = new HashMap<>();
    private final Map<Long, Apartment> apartmentTable = new HashMap<>();
    private final Map<Long, Resident> residentTable = new HashMap<>();
    private final Map<Long, User> userTable = new HashMap<>();

    private InvoiceRepository invoiceRepository;
    private PaymentRepository paymentRepository;
    private SocietyRepository societyRepository;
    private ApartmentRepository apartmentRepository;
    private ResidentRepository residentRepository;
    private UserRepository userRepository;

    private BillingAndPaymentService billingAndPaymentService;

    @SuppressWarnings("unchecked")
    private <T> T createProxy(Class<T> intf, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(intf.getClassLoader(), new Class<?>[]{intf}, handler);
    }

    @BeforeEach
    void setUp() {
        invoiceTable.clear();
        paymentTable.clear();
        societyTable.clear();
        towerTable.clear();
        apartmentTable.clear();
        residentTable.clear();
        userTable.clear();

        invoiceRepository = createProxy(InvoiceRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("save".equals(name)) {
                Invoice i = (Invoice) args[0];
                if (i.getId() == null) {
                    i.setId((long) (invoiceTable.size() + 1));
                }
                invoiceTable.put(i.getId(), i);
                return i;
            } else if ("findById".equals(name)) {
                Long id = (Long) args[0];
                return Optional.ofNullable(invoiceTable.get(id));
            } else if ("existsByApartmentIdAndBillingMonth".equals(name)) {
                Long aId = (Long) args[0];
                String m = (String) args[1];
                return invoiceTable.values().stream()
                        .anyMatch(i -> i.getApartment().getId().equals(aId) && i.getBillingMonth().equals(m));
            } else if ("findByResidentId".equals(name)) {
                Long rId = (Long) args[0];
                return invoiceTable.values().stream().filter(i -> i.getResident().getId().equals(rId)).toList();
            } else if ("findBySocietyId".equals(name)) {
                Long sId = (Long) args[0];
                return invoiceTable.values().stream().filter(i -> i.getSociety().getId().equals(sId)).toList();
            } else if ("existsById".equals(name)) {
                Long id = (Long) args[0];
                return invoiceTable.containsKey(id);
            }
            return null;
        });

        paymentRepository = createProxy(PaymentRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("save".equals(name)) {
                Payment p = (Payment) args[0];
                if (p.getId() == null) {
                    p.setId((long) (paymentTable.size() + 1));
                }
                paymentTable.put(p.getId(), p);
                return p;
            } else if ("findByRazorpayOrderId".equals(name)) {
                String orderId = (String) args[0];
                return paymentTable.values().stream().filter(p -> p.getRazorpayOrderId().equals(orderId)).findFirst();
            } else if ("findByInvoiceId".equals(name)) {
                Long invId = (Long) args[0];
                return paymentTable.values().stream().filter(p -> p.getInvoice().getId().equals(invId)).toList();
            }
            return null;
        });

        societyRepository = createProxy(SocietyRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findById".equals(name)) {
                Long id = (Long) args[0];
                return Optional.ofNullable(societyTable.get(id));
            } else if ("existsById".equals(name)) {
                Long id = (Long) args[0];
                return societyTable.containsKey(id);
            }
            return null;
        });

        apartmentRepository = createProxy(ApartmentRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findByTowerSocietyId".equals(name)) {
                Long sId = (Long) args[0];
                return apartmentTable.values().stream()
                        .filter(a -> a.getTower().getSociety().getId().equals(sId))
                        .toList();
            }
            return null;
        });

        residentRepository = createProxy(ResidentRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findByApartmentId".equals(name)) {
                Long aId = (Long) args[0];
                return residentTable.values().stream().filter(r -> r.getApartment().getId().equals(aId)).findFirst();
            } else if ("findByUserId".equals(name)) {
                Long uId = (Long) args[0];
                return residentTable.values().stream().filter(r -> r.getUser().getId().equals(uId)).findFirst();
            } else if ("save".equals(name)) {
                Resident r = (Resident) args[0];
                residentTable.put(r.getId(), r);
                return r;
            }
            return null;
        });

        userRepository = createProxy(UserRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findById".equals(name)) {
                Long id = (Long) args[0];
                return Optional.ofNullable(userTable.get(id));
            }
            return null;
        });

        billingAndPaymentService = new BillingAndPaymentServiceImpl(
                invoiceRepository,
                paymentRepository,
                societyRepository,
                apartmentRepository,
                residentRepository,
                userRepository
        );

        // Seed Society & Tower
        Society society = new Society();
        society.setId(1L);
        society.setName("Hiranandani Gardens");
        society.setBasePoolingFee(new BigDecimal("4200.00"));
        societyTable.put(1L, society);

        Tower tower = new Tower();
        tower.setId(10L);
        tower.setName("Tower A");
        tower.setSociety(society);
        towerTable.put(10L, tower);

        // Seed Resident, User & Flat (with ₹280 wallet credit from 2 skipped meals)
        User residentUser = new User();
        residentUser.setId(101L);
        residentUser.setFullName("Aarav Sharma");
        residentUser.setEmail("aarav.sharma@example.com");
        residentUser.setPhone("+91 98111 22334");
        userTable.put(101L, residentUser);

        Apartment apt = new Apartment();
        apt.setId(201L);
        apt.setTower(tower);
        apt.setFlatNumber("Flat 302");
        apt.setFloorNumber(3);
        apartmentTable.put(201L, apt);

        Resident resident = new Resident();
        resident.setId(301L);
        resident.setUser(residentUser);
        resident.setSociety(society);
        resident.setApartment(apt);
        resident.setWalletBalance(new BigDecimal("280.00"));
        residentTable.put(301L, resident);

        // Seed Admin User
        User admin = new User();
        admin.setId(1L);
        admin.setFullName("Society Administrator");
        userTable.put(1L, admin);
    }

    @Test
    @DisplayName("TC-01: Generate monthly invoices and apply wallet skip credits")
    void testGenerateMonthlyInvoicesWithCredits() {
        GenerateInvoicesRequest req = new GenerateInvoicesRequest("2026-11", LocalDate.now().plusDays(10));
        List<InvoiceDto> invoices = billingAndPaymentService.generateMonthlyInvoices(1L, req);

        assertEquals(1, invoices.size());
        InvoiceDto inv = invoices.get(0);
        assertEquals("INV-202611-Flat302", inv.getInvoiceNumber());
        assertEquals("2026-11", inv.getBillingMonth());
        assertEquals(new BigDecimal("4200.00"), inv.getBaseAmount());
        assertEquals(new BigDecimal("280.00"), inv.getDiscountWallet());
        assertEquals(new BigDecimal("3920.00"), inv.getNetAmount());
        assertEquals("UNPAID", inv.getStatus());

        // Wallet balance deducted to zero
        Resident updatedRes = residentTable.get(301L);
        assertEquals(0, updatedRes.getWalletBalance().compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("TC-02: Prevent duplicate invoice generation for same month and flat")
    void testPreventDuplicateMonthlyInvoice() {
        GenerateInvoicesRequest req = new GenerateInvoicesRequest("2026-11", LocalDate.now().plusDays(10));
        billingAndPaymentService.generateMonthlyInvoices(1L, req);

        // Run second time
        List<InvoiceDto> secondRun = billingAndPaymentService.generateMonthlyInvoices(1L, req);
        assertEquals(0, secondRun.size());
    }

    @Test
    @DisplayName("TC-03: Create Razorpay test order for an invoice")
    void testCreateRazorpayOrder() {
        GenerateInvoicesRequest req = new GenerateInvoicesRequest("2026-11", LocalDate.now().plusDays(10));
        InvoiceDto inv = billingAndPaymentService.generateMonthlyInvoices(1L, req).get(0);

        CreateRazorpayOrderRequest orderReq = new CreateRazorpayOrderRequest(inv.getId());
        RazorpayOrderResponse order = billingAndPaymentService.createRazorpayOrder(101L, orderReq);

        assertNotNull(order);
        assertTrue(order.getOrderId().startsWith("order_test_"));
        assertEquals(new BigDecimal("3920.00"), order.getAmount());
        assertEquals(392000L, order.getAmountInPaise());
        assertEquals("INR", order.getCurrency());
        assertEquals("rzp_test_CookCircleKey123", order.getKeyId());
        assertEquals("Aarav Sharma", order.getResidentName());
    }

    @Test
    @DisplayName("TC-04: Verify Razorpay payment signature and mark invoice as PAID")
    void testVerifyRazorpayPaymentSuccess() {
        GenerateInvoicesRequest req = new GenerateInvoicesRequest("2026-11", LocalDate.now().plusDays(10));
        InvoiceDto inv = billingAndPaymentService.generateMonthlyInvoices(1L, req).get(0);

        CreateRazorpayOrderRequest orderReq = new CreateRazorpayOrderRequest(inv.getId());
        RazorpayOrderResponse order = billingAndPaymentService.createRazorpayOrder(101L, orderReq);

        String paymentId = "pay_test_987654321";
        String payload = order.getOrderId() + "|" + paymentId;
        String validSignature = BillingAndPaymentServiceImpl.calculateHmacSha256(payload, "rzp_test_secret_CookCircleSecureHMAC");

        VerifyPaymentRequest verifyReq = new VerifyPaymentRequest(order.getOrderId(), paymentId, validSignature);
        PaymentDto payment = billingAndPaymentService.verifyRazorpayPayment(verifyReq);

        assertNotNull(payment);
        assertEquals("SUCCESS", payment.getStatus());
        assertEquals(paymentId, payment.getRazorpayPaymentId());
        assertNotNull(payment.getVerifiedAt());

        // Invoice is now PAID
        Invoice updatedInvoice = invoiceTable.get(inv.getId());
        assertEquals("PAID", updatedInvoice.getStatus());
        assertNotNull(updatedInvoice.getPaidAt());
    }

    @Test
    @DisplayName("TC-05: Reject tampered or invalid Razorpay signature")
    void testVerifyRazorpayPaymentFailure() {
        GenerateInvoicesRequest req = new GenerateInvoicesRequest("2026-11", LocalDate.now().plusDays(10));
        InvoiceDto inv = billingAndPaymentService.generateMonthlyInvoices(1L, req).get(0);

        CreateRazorpayOrderRequest orderReq = new CreateRazorpayOrderRequest(inv.getId());
        RazorpayOrderResponse order = billingAndPaymentService.createRazorpayOrder(101L, orderReq);

        VerifyPaymentRequest invalidReq = new VerifyPaymentRequest(order.getOrderId(), "pay_tampered_123", "invalid_tampered_signature_hex");

        assertThrows(IllegalArgumentException.class, () -> {
            billingAndPaymentService.verifyRazorpayPayment(invalidReq);
        });

        // Invoice remains UNPAID
        Invoice invoice = invoiceTable.get(inv.getId());
        assertEquals("UNPAID", invoice.getStatus());
    }

    @Test
    @DisplayName("TC-06: Record offline cash / manual payment by society administrator")
    void testRecordOfflinePayment() {
        GenerateInvoicesRequest req = new GenerateInvoicesRequest("2026-11", LocalDate.now().plusDays(10));
        InvoiceDto inv = billingAndPaymentService.generateMonthlyInvoices(1L, req).get(0);

        RecordOfflinePaymentRequest offlineReq = new RecordOfflinePaymentRequest(
                inv.getId(),
                new BigDecimal("3920.00"),
                "CASH",
                "RECEIPT-CASH-8891"
        );

        PaymentDto payment = billingAndPaymentService.recordOfflinePayment(1L, offlineReq);

        assertNotNull(payment);
        assertEquals("SUCCESS", payment.getStatus());
        assertEquals("CASH", payment.getMethod());
        assertEquals("RECEIPT-CASH-8891", payment.getRazorpayPaymentId());

        // Invoice is now PAID
        Invoice updatedInvoice = invoiceTable.get(inv.getId());
        assertEquals("PAID", updatedInvoice.getStatus());
    }
}
