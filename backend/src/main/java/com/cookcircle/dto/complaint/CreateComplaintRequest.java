package com.cookcircle.dto.complaint;

import jakarta.validation.constraints.NotBlank;

public class CreateComplaintRequest {

    @NotBlank(message = "Category is required (Punctuality, Taste, Hygiene, Billing)")
    private String category;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Description is required")
    private String description;

    public CreateComplaintRequest() {}

    public CreateComplaintRequest(String category, String subject, String description) {
        this.category = category;
        this.subject = subject;
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

