package com.cookcircle.dto.society;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateTowerRequest {

    @NotBlank(message = "Tower name is required")
    private String name;

    @NotNull(message = "Total floors is required")
    private Integer totalFloors = 20;

    public CreateTowerRequest() {}

    public CreateTowerRequest(String name, Integer totalFloors) {
        this.name = name;
        this.totalFloors = totalFloors;
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
}

