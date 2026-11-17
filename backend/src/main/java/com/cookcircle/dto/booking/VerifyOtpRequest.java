package com.cookcircle.dto.booking;

import jakarta.validation.constraints.NotBlank;

public class VerifyOtpRequest {

    @NotBlank(message = "Arrival OTP is required")
    private String otp;

    public VerifyOtpRequest() {}

    public VerifyOtpRequest(String otp) {
        this.otp = otp;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}

