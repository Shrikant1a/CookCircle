package com.cookcircle.dto.cook;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class AssignCookRequest {

    @NotNull(message = "Tower ID is required")
    private Long towerId;

    @NotBlank(message = "Shift is required (MORNING, EVENING, BOTH)")
    private String shift = "MORNING";

    private LocalDate startDate = LocalDate.now();

    private LocalDate endDate;

    public AssignCookRequest() {}

    public AssignCookRequest(Long towerId, String shift, LocalDate startDate, LocalDate endDate) {
        this.towerId = towerId;
        this.shift = shift;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getTowerId() {
        return towerId;
    }

    public void setTowerId(Long towerId) {
        this.towerId = towerId;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}

