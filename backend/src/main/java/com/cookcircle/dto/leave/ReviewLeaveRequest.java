package com.cookcircle.dto.leave;

public class ReviewLeaveRequest {

    private Long backupCookId;
    private String actionNotes;

    public ReviewLeaveRequest() {}

    public ReviewLeaveRequest(Long backupCookId, String actionNotes) {
        this.backupCookId = backupCookId;
        this.actionNotes = actionNotes;
    }

    public Long getBackupCookId() {
        return backupCookId;
    }

    public void setBackupCookId(Long backupCookId) {
        this.backupCookId = backupCookId;
    }

    public String getActionNotes() {
        return actionNotes;
    }

    public void setActionNotes(String actionNotes) {
        this.actionNotes = actionNotes;
    }
}

