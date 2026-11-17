package com.cookcircle.dto.booking;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CreateBookingRequest {

    @NotNull(message = "Slot ID is required")
    private Long slotId;

    @NotNull(message = "Booking date is required")
    private LocalDate bookingDate;

    private String dishesPlanned = "Dal Tadka, Paneer Bhurji, Phulkas, Steamed Rice";

    public CreateBookingRequest() {}

    public CreateBookingRequest(Long slotId, LocalDate bookingDate, String dishesPlanned) {
        this.slotId = slotId;
        this.bookingDate = bookingDate;
        this.dishesPlanned = dishesPlanned;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getDishesPlanned() {
        return dishesPlanned;
    }

    public void setDishesPlanned(String dishesPlanned) {
        this.dishesPlanned = dishesPlanned;
    }
}

