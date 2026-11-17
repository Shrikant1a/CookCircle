package com.cookcircle.dto.attendance;

import jakarta.validation.constraints.NotNull;

public class LogAttendanceRequest {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    private String status = "ON_TIME"; // ON_TIME, DELAYED, ABSENT

    private String remarks;

    public LogAttendanceRequest() {}

    public LogAttendanceRequest(Long bookingId, String status, String remarks) {
        this.bookingId = bookingId;
        this.status = status;
        this.remarks = remarks;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}

