package com.cookcircle.service;

import com.cookcircle.dto.billing.*;

import java.util.List;

public interface BillingAndPaymentService {
    List<InvoiceDto> generateMonthlyInvoices(Long societyId, GenerateInvoicesRequest request);
    List<InvoiceDto> getSocietyInvoices(Long societyId, String billingMonth, String status);
    List<InvoiceDto> getResidentInvoices(Long residentUserId);
    InvoiceDto getInvoiceById(Long id);

    RazorpayOrderResponse createRazorpayOrder(Long residentUserId, CreateRazorpayOrderRequest request);
    PaymentDto verifyRazorpayPayment(VerifyPaymentRequest request);
    PaymentDto recordOfflinePayment(Long adminUserId, RecordOfflinePaymentRequest request);
    List<PaymentDto> getInvoicePayments(Long invoiceId);
}

