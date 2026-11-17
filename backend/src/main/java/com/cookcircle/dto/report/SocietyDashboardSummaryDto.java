package com.cookcircle.dto.report;

import java.math.BigDecimal;

public class SocietyDashboardSummaryDto {

    private Long societyId;
    private String societyName;
    private long totalApartments;
    private long totalResidents;
    private long totalCooks;
    private long backupCooks;
    private long todayBookings;
    private long pendingLeaves;
    private BigDecimal monthlyCollection = BigDecimal.ZERO;
    private BigDecimal pendingCollection = BigDecimal.ZERO;
    private double poolUtilizationRate = 94.5;
    private double onTimeSlaRate = 98.2;

    public SocietyDashboardSummaryDto() {}

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

    public long getTotalApartments() {
        return totalApartments;
    }

    public void setTotalApartments(long totalApartments) {
        this.totalApartments = totalApartments;
    }

    public long getTotalResidents() {
        return totalResidents;
    }

    public void setTotalResidents(long totalResidents) {
        this.totalResidents = totalResidents;
    }

    public long getTotalCooks() {
        return totalCooks;
    }

    public void setTotalCooks(long totalCooks) {
        this.totalCooks = totalCooks;
    }

    public long getBackupCooks() {
        return backupCooks;
    }

    public void setBackupCooks(long backupCooks) {
        this.backupCooks = backupCooks;
    }

    public long getTodayBookings() {
        return todayBookings;
    }

    public void setTodayBookings(long todayBookings) {
        this.todayBookings = todayBookings;
    }

    public long getPendingLeaves() {
        return pendingLeaves;
    }

    public void setPendingLeaves(long pendingLeaves) {
        this.pendingLeaves = pendingLeaves;
    }

    public BigDecimal getMonthlyCollection() {
        return monthlyCollection;
    }

    public void setMonthlyCollection(BigDecimal monthlyCollection) {
        this.monthlyCollection = monthlyCollection;
    }

    public BigDecimal getPendingCollection() {
        return pendingCollection;
    }

    public void setPendingCollection(BigDecimal pendingCollection) {
        this.pendingCollection = pendingCollection;
    }

    public double getPoolUtilizationRate() {
        return poolUtilizationRate;
    }

    public void setPoolUtilizationRate(double poolUtilizationRate) {
        this.poolUtilizationRate = poolUtilizationRate;
    }

    public double getOnTimeSlaRate() {
        return onTimeSlaRate;
    }

    public void setOnTimeSlaRate(double onTimeSlaRate) {
        this.onTimeSlaRate = onTimeSlaRate;
    }
}

