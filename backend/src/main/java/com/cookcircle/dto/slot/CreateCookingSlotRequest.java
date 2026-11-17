package com.cookcircle.dto.slot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public class CreateCookingSlotRequest {

    @NotBlank(message = "Shift is required (MORNING or EVENING)")
    private String shift;

    @NotNull(message = "Slot order is required (1 to 5)")
    private Integer slotOrder;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    public CreateCookingSlotRequest() {}

    public CreateCookingSlotRequest(String shift, Integer slotOrder, LocalTime startTime, LocalTime endTime) {
        this.shift = shift;
        this.slotOrder = slotOrder;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public Integer getSlotOrder() {
        return slotOrder;
    }

    public void setSlotOrder(Integer slotOrder) {
        this.slotOrder = slotOrder;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}

