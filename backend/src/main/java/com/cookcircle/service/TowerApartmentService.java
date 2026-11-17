package com.cookcircle.service;

import com.cookcircle.dto.apartment.ApartmentDto;
import com.cookcircle.dto.apartment.CreateApartmentRequest;
import com.cookcircle.dto.society.CreateTowerRequest;
import com.cookcircle.dto.society.TowerDto;

import java.util.List;

public interface TowerApartmentService {
    List<TowerDto> getTowersBySociety(Long societyId);
    TowerDto getTowerById(Long towerId);
    TowerDto createTower(Long societyId, CreateTowerRequest request);
    void deleteTower(Long towerId);

    List<ApartmentDto> getApartmentsByTower(Long towerId);
    List<ApartmentDto> getApartmentsBySociety(Long societyId);
    ApartmentDto getApartmentById(Long apartmentId);
    ApartmentDto createApartment(Long towerId, CreateApartmentRequest request);
    ApartmentDto updateApartmentStatus(Long apartmentId, String status);
}

