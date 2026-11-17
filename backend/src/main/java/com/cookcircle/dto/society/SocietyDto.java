package com.cookcircle.dto.society;

import java.math.BigDecimal;
import java.util.List;

public class SocietyDto {

    private Long id;
    private String code;
    private String name;
    private String addressLine;
    private String city;
    private String state;
    private String postalCode;
    private BigDecimal basePoolingFee;
    private BigDecimal skipMealCredit;
    private String status;
    private List<TowerDto> towers;

    public SocietyDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public BigDecimal getBasePoolingFee() {
        return basePoolingFee;
    }

    public void setBasePoolingFee(BigDecimal basePoolingFee) {
        this.basePoolingFee = basePoolingFee;
    }

    public BigDecimal getSkipMealCredit() {
        return skipMealCredit;
    }

    public void setSkipMealCredit(BigDecimal skipMealCredit) {
        this.skipMealCredit = skipMealCredit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<TowerDto> getTowers() {
        return towers;
    }

    public void setTowers(List<TowerDto> towers) {
        this.towers = towers;
    }
}

