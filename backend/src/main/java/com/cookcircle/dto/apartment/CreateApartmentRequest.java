package com.cookcircle.dto.apartment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateApartmentRequest {

    @NotBlank(message = "Flat number is required")
    private String flatNumber;

    @NotNull(message = "Floor number is required")
    private Integer floorNumber;

    private String configuration = "2BHK";
    private String status = "OCCUPIED";

    public CreateApartmentRequest() {}

    public CreateApartmentRequest(String flatNumber, Integer floorNumber, String configuration) {
        this.flatNumber = flatNumber;
        this.floorNumber = floorNumber;
        this.configuration = configuration;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public void setFlatNumber(String flatNumber) {
        this.flatNumber = flatNumber;
    }

    public Integer getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(Integer floorNumber) {
        this.floorNumber = floorNumber;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

