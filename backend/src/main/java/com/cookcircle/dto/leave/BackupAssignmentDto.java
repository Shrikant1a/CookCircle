package com.cookcircle.dto.leave;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BackupAssignmentDto {

    private Long id;
    private Long leaveRequestId;
    private Long primaryCookId;
    private String primaryCookName;
    private Long backupCookId;
    private String backupCookName;
    private Long towerId;
    private String towerName;
    private LocalDate assignmentDate;
    private String status;
    private String assignedByName;
    private LocalDateTime createdAt;

    public BackupAssignmentDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLeaveRequestId() {
        return leaveRequestId;
    }

    public void setLeaveRequestId(Long leaveRequestId) {
        this.leaveRequestId = leaveRequestId;
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

    public Long getBackupCookId() {
        return backupCookId;
    }

    public void setBackupCookId(Long backupCookId) {
        this.backupCookId = backupCookId;
    }

    public String getBackupCookName() {
        return backupCookName;
    }

    public void setBackupCookName(String backupCookName) {
        this.backupCookName = backupCookName;
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

    public LocalDate getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(LocalDate assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignedByName() {
        return assignedByName;
    }

    public void setAssignedByName(String assignedByName) {
        this.assignedByName = assignedByName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

