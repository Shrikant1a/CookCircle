package com.cookcircle.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", uniqueConstraints = {
    @UniqueConstraint(name = "uk_slot_date_status", columnNames = {"slot_id", "booking_date", "status"})
})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "society_id", nullable = false)
    private Society society;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private CookingSlot slot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_cook_id", nullable = false)
    private Cook primaryCook;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "effective_cook_id", nullable = false)
    private Cook effectiveCook;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "dishes_planned", columnDefinition = "TEXT")
    private String dishesPlanned;

    @Column(name = "arrival_otp", nullable = false, length = 6)
    private String arrivalOtp;

    @Column(nullable = false, length = 30)
    private String status = "CONFIRMED"; // CONFIRMED, COOKING, COMPLETED, SKIPPED, CANCELLED

    @Column(name = "skip_refund_amount", precision = 10, scale = 2)
    private BigDecimal skipRefundAmount = BigDecimal.ZERO;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Booking() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Society getSociety() {
        return society;
    }

    public void setSociety(Society society) {
        this.society = society;
    }

    public CookingSlot getSlot() {
        return slot;
    }

    public void setSlot(CookingSlot slot) {
        this.slot = slot;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public Cook getPrimaryCook() {
        return primaryCook;
    }

    public void setPrimaryCook(Cook primaryCook) {
        this.primaryCook = primaryCook;
    }

    public Cook getEffectiveCook() {
        return effectiveCook;
    }

    public void setEffectiveCook(Cook effectiveCook) {
        this.effectiveCook = effectiveCook;
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
