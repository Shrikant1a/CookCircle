package com.cookcircle.dto.society;

public class TowerDto {

    private Long id;
    private Long societyId;
    private String societyName;
    private String name;
    private Integer totalFloors;
    private Integer totalApartments;

    public TowerDto() {}

    public TowerDto(Long id, Long societyId, String societyName, String name, Integer totalFloors) {
        this.id = id;
        this.societyId = societyId;
        this.societyName = societyName;
        this.name = name;
        this.totalFloors = totalFloors;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTotalFloors() {
        return totalFloors;
    }

    public void setTotalFloors(Integer totalFloors) {
        this.totalFloors = totalFloors;
    }

    public Integer getTotalApartments() {
        return totalApartments;
    }

    public void setTotalApartments(Integer totalApartments) {
        this.totalApartments = totalApartments;
    }
}

