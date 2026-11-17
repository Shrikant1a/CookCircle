package com.cookcircle.dto.resident;

public class UpdateResidentPreferencesRequest {

    private String householdSize;
    private String dietaryPref;
    private String phone;

    public UpdateResidentPreferencesRequest() {}

    public UpdateResidentPreferencesRequest(String householdSize, String dietaryPref, String phone) {
        this.householdSize = householdSize;
        this.dietaryPref = dietaryPref;
        this.phone = phone;
    }

    public String getHouseholdSize() {
        return householdSize;
    }

    public void setHouseholdSize(String householdSize) {
        this.householdSize = householdSize;
    }

    public String getDietaryPref() {
        return dietaryPref;
    }

    public void setDietaryPref(String dietaryPref) {
        this.dietaryPref = dietaryPref;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

