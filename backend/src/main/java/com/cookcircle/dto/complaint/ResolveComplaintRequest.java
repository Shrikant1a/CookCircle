package com.cookcircle.dto.complaint;

import jakarta.validation.constraints.NotBlank;

public class ResolveComplaintRequest {

    @NotBlank(message = "Resolution notes are required")
    private String resolutionNotes;

    public ResolveComplaintRequest() {}

    public ResolveComplaintRequest(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }
}

