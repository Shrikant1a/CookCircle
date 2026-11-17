package com.cookcircle.dto.leave;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeaveRequestDto {

    private Long id;
    private Long cookId;
    private String cookName;
    private Long societyId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String status;
    private String actionByName;
    private String actionNotes;
    private Integer affectedBookingsCount;
    private String backupCookName;
    private LocalDateTime createdAt;

    public LeaveRequestDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCookId() {
        return cookId;
    }

    public void setCookId(Long cookId) {
        this.cookId = cookId;
    }

    public String getCookName() {
        return cookName;
    }

    public void setCookName(String cookName) {
        this.cookName = cookName;
    }

    public Long getSocietyId() {
        return societyId;
    }

    public void setSocietyId(Long societyId) {
        this.societyId = societyId;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getActionByName() {
        return actionByName;
    }

    public void setActionByName(String actionByName) {
        this.actionByName = actionByName;
    }

    public String getActionNotes() {
        return actionNotes;
    }

    public void setActionNotes(String actionNotes) {
        this.actionNotes = actionNotes;
    }

    public Integer getAffectedBookingsCount() {
        return affectedBookingsCount;
    }

    public void setAffectedBookingsCount(Integer affectedBookingsCount) {
        this.affectedBookingsCount = affectedBookingsCount;
    }

    public String getBackupCookName() {
        return backupCookName;
    }

    public void setBackupCookName(String backupCookName) {
        this.backupCookName = backupCookName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

