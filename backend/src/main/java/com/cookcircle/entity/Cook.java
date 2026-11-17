package com.cookcircle.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cooks")
public class Cook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "society_id", nullable = false)
    private Society society;

    @Column(nullable = false, length = 30)
    private String type; // PRIMARY_COOK or BACKUP_FLOAT_COOK

    @Column(precision = 3, scale = 2)
    private BigDecimal rating = new BigDecimal("4.90");

    @Column(nullable = false, length = 255)
    private String specialties;

    @Column(name = "experience_years", nullable = false)
    private Integer experienceYears = 5;

    @Column(name = "aadhaar_verified")
    private Boolean aadhaarVerified = true;

    @Column(name = "police_verified")
    private Boolean policeVerified = true;

    @Column(name = "vaccination_status", length = 60)
    private String vaccinationStatus = "Double Vaccinated";

    @Column(nullable = false, length = 30)
    private String status = "ACTIVE"; // ACTIVE, ON_LEAVE, INACTIVE

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Cook() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Society getSociety() {
        return society;
    }

    public void setSociety(Society society) {
        this.society = society;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
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

    public Boolean getAadhaarVerified() {
        return aadhaarVerified;
    }

    public void setAadhaarVerified(Boolean aadhaarVerified) {
        this.aadhaarVerified = aadhaarVerified;
    }

    public Boolean getPoliceVerified() {
        return policeVerified;
    }

    public void setPoliceVerified(Boolean policeVerified) {
        this.policeVerified = policeVerified;
    }

    public String getVaccinationStatus() {
        return vaccinationStatus;
    }

    public void setVaccinationStatus(String vaccinationStatus) {
        this.vaccinationStatus = vaccinationStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
