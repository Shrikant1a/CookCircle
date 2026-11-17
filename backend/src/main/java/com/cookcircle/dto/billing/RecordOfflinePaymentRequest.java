package com.cookcircle.dto.billing;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class RecordOfflinePaymentRequest {

    @NotNull(message = "Invoice ID is required")
    private Long invoiceId;

    @NotNull(message = "Payment amount is required")
    private BigDecimal amount;

    @NotBlank(message = "Payment method is required (e.g. CASH, UPI, CHEQUE)")
    private String method = "CASH";

    private String referenceNumber;

    public RecordOfflinePaymentRequest() {}

    public RecordOfflinePaymentRequest(Long invoiceId, BigDecimal amount, String method, String referenceNumber) {
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.method = method;
        this.referenceNumber = referenceNumber;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
}

