package com.cookcircle.dto.slot;

import java.time.LocalTime;

public class CookingSlotDto {

    private Long id;
    private Long towerId;
    private String towerName;
    private String shift;
    private Integer slotOrder;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isActive;

    public CookingSlotDto() {}

    public CookingSlotDto(Long id, Long towerId, String towerName, String shift, Integer slotOrder,
                          LocalTime startTime, LocalTime endTime, Boolean isActive) {
        this.id = id;
        this.towerId = towerId;
        this.towerName = towerName;
        this.shift = shift;
        this.slotOrder = slotOrder;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public Integer getSlotOrder() {
        return slotOrder;
    }

    public void setSlotOrder(Integer slotOrder) {
        this.slotOrder = slotOrder;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}

