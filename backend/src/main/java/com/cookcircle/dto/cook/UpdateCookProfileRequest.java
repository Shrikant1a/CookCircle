package com.cookcircle.dto.cook;

public class UpdateCookProfileRequest {

    private String phone;
    private String specialties;
    private Integer experienceYears;
    private String vaccinationStatus;

    public UpdateCookProfileRequest() {}

    public UpdateCookProfileRequest(String phone, String specialties, Integer experienceYears, String vaccinationStatus) {
        this.phone = phone;
        this.specialties = specialties;
        this.experienceYears = experienceYears;
        this.vaccinationStatus = vaccinationStatus;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSpecialties() {
        return specialties;
    }

    public void setSpecialties(String specialties) {
        this.specialties = specialties;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getVaccinationStatus() {
        return vaccinationStatus;
    }

    public void setVaccinationStatus(String vaccinationStatus) {
        this.vaccinationStatus = vaccinationStatus;
    }
}

