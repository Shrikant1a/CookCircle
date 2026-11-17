package com.cookcircle.dto.booking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class BookingDto {

    private Long id;
    private Long societyId;
    private String societyName;
    private Long towerId;
    private String towerName;
    private Long slotId;
    private Integer slotOrder;
    private String shift;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long apartmentId;
    private String flatNumber;
    private Long residentId;
    private String residentName;
    private String residentPhone;
    private Long primaryCookId;
    private String primaryCookName;
    private Long effectiveCookId;
    private String effectiveCookName;
    private LocalDate bookingDate;
    private String dishesPlanned;
    private String arrivalOtp;
    private String status;
    private BigDecimal skipRefundAmount;
    private LocalDateTime createdAt;

    public BookingDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSocietyId() {
        return societyId;
    }

    public void setSocietyId(Long societyId) {
        this.societyId = societyId;
    }

    public String getSocietyName() {
        return societyName;
    }

    public void setSocietyName(String societyName) {
        this.societyName = societyName;
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

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public Integer getSlotOrder() {
        return slotOrder;
    }

    public void setSlotOrder(Integer slotOrder) {
        this.slotOrder = slotOrder;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
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

    public Long getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Long apartmentId) {
        this.apartmentId = apartmentId;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public void setFlatNumber(String flatNumber) {
        this.flatNumber = flatNumber;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public String getResidentName() {
        return residentName;
    }

    public void setResidentName(String residentName) {
        this.residentName = residentName;
    }

    public String getResidentPhone() {
        return residentPhone;
    }

    public void setResidentPhone(String residentPhone) {
        this.residentPhone = residentPhone;
    }

    public Long getPrimaryCookId() {
        return primaryCookId;
    }

    public void setPrimaryCookId(Long primaryCookId) {
        this.primaryCookId = primaryCookId;
    }

    public String getPrimaryCookName() {
        return primaryCookName;
    }

    public void setPrimaryCookName(String primaryCookName) {
        this.primaryCookName = primaryCookName;
    }

    public Long getEffectiveCookId() {
        return effectiveCookId;
    }

    public void setEffectiveCookId(Long effectiveCookId) {
        this.effectiveCookId = effectiveCookId;
    }

    public String getEffectiveCookName() {
        return effectiveCookName;
    }

    public void setEffectiveCookName(String effectiveCookName) {
        this.effectiveCookName = effectiveCookName;
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

    public String getArrivalOtp() {
        return arrivalOtp;
    }

    public void setArrivalOtp(String arrivalOtp) {
        this.arrivalOtp = arrivalOtp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getSkipRefundAmount() {
        return skipRefundAmount;
    }

    public void setSkipRefundAmount(BigDecimal skipRefundAmount) {
        this.skipRefundAmount = skipRefundAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

