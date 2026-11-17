package com.cookcircle.dto.society;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreateSocietyRequest {

    @NotBlank(message = "Society code is required")
    private String code;

    @NotBlank(message = "Society name is required")
    private String name;

    @NotBlank(message = "Address line is required")
    private String addressLine;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Postal code is required")
    private String postalCode;

    @NotNull(message = "Base pooling fee is required")
    private BigDecimal basePoolingFee = new BigDecimal("4200.00");

    @NotNull(message = "Skip meal credit is required")
    private BigDecimal skipMealCredit = new BigDecimal("140.00");

    public CreateSocietyRequest() {}

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public BigDecimal getBasePoolingFee() {
        return basePoolingFee;
    }

    public void setBasePoolingFee(BigDecimal basePoolingFee) {
        this.basePoolingFee = basePoolingFee;
    }

    public BigDecimal getSkipMealCredit() {
        return skipMealCredit;
    }

    public void setSkipMealCredit(BigDecimal skipMealCredit) {
        this.skipMealCredit = skipMealCredit;
    }
}

