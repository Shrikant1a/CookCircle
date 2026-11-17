package com.cookcircle.dto.billing;

import jakarta.validation.constraints.NotNull;

public class CreateRazorpayOrderRequest {

    @NotNull(message = "Invoice ID is required")
    private Long invoiceId;

    public CreateRazorpayOrderRequest() {}

    public CreateRazorpayOrderRequest(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }
}

