package com.cookcircle.dto.auth;

public class UserSummaryDto {

    private Long id;
    private String email;
    private String phone;
    private String fullName;
    private String role;
    private Long societyId;
    private String societyName;
    private String flatNumber;

    public UserSummaryDto() {}

    public UserSummaryDto(Long id, String email, String phone, String fullName, String role, Long societyId, String societyName) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.fullName = fullName;
        this.role = role;
        this.societyId = societyId;
        this.societyName = societyName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public String getFlatNumber() {
        return flatNumber;
    }

    public void setFlatNumber(String flatNumber) {
        this.flatNumber = flatNumber;
    }
}

