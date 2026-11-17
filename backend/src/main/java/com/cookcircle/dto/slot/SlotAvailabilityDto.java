package com.cookcircle.dto.slot;

import java.time.LocalDate;
import java.time.LocalTime;

public class SlotAvailabilityDto {

    private Long slotId;
    private Long towerId;
    private String towerName;
    private String shift;
    private Integer slotOrder;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate date;
    private boolean isAvailable;
    private Long bookingId;
    private String bookedFlatNumber;
    private String primaryCookName;
    private String effectiveCookName;
    private String bookingStatus;

    public SlotAvailabilityDto() {}

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public Long getTowerId() {
        return towerId;
    }

    public void setTowerId(Long towerId) {
        this.towerId = towerId;
    }

    public String getTowerName() {
        return towerName;
    }

    public void setTowerName(String towerName) {
        this.towerName = towerName;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookedFlatNumber() {
        return bookedFlatNumber;
    }

    public void setBookedFlatNumber(String bookedFlatNumber) {
        this.bookedFlatNumber = bookedFlatNumber;
    }

    public String getPrimaryCookName() {
        return primaryCookName;
    }

    public void setPrimaryCookName(String primaryCookName) {
        this.primaryCookName = primaryCookName;
    }

    public String getEffectiveCookName() {
        return effectiveCookName;
    }

    public void setEffectiveCookName(String effectiveCookName) {
        this.effectiveCookName = effectiveCookName;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
}

