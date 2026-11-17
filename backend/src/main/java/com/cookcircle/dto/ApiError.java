package com.cookcircle.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private String code;
    private String message;
    private int status;
    private List<String> details;
    private Instant timestamp;

    public ApiError() {
        this.timestamp = Instant.now();
    }

    public ApiError(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.timestamp = Instant.now();
    }

    public ApiError(String code, String message, int status, List<String> details) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.details = details;
        this.timestamp = Instant.now();
    }

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
