package com.cookcircle.dto.billing;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class GenerateInvoicesRequest {

    @NotBlank(message = "Billing month is required (e.g. 2026-11)")
    private String billingMonth;

    private LocalDate dueDate;

    public GenerateInvoicesRequest() {}

    public GenerateInvoicesRequest(String billingMonth, LocalDate dueDate) {
        this.billingMonth = billingMonth;
        this.dueDate = dueDate;
    }

    public String getBillingMonth() {
        return billingMonth;
    }

    public void setBillingMonth(String billingMonth) {
        this.billingMonth = billingMonth;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}

