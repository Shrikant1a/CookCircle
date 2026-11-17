package com.cookcircle.service.impl;

import com.cookcircle.dto.apartment.ApartmentDto;
import com.cookcircle.dto.apartment.CreateApartmentRequest;
import com.cookcircle.dto.society.CreateTowerRequest;
import com.cookcircle.dto.society.TowerDto;
import com.cookcircle.entity.Apartment;
import com.cookcircle.entity.Resident;
import com.cookcircle.entity.Society;
import com.cookcircle.entity.Tower;
import com.cookcircle.exception.ResourceNotFoundException;
import com.cookcircle.repository.ApartmentRepository;
import com.cookcircle.repository.ResidentRepository;
import com.cookcircle.repository.SocietyRepository;
import com.cookcircle.repository.TowerRepository;
import com.cookcircle.service.TowerApartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TowerApartmentServiceImpl implements TowerApartmentService {

    private final SocietyRepository societyRepository;
    private final TowerRepository towerRepository;
    private final ApartmentRepository apartmentRepository;
    private final ResidentRepository residentRepository;

    public TowerApartmentServiceImpl(SocietyRepository societyRepository,
                                     TowerRepository towerRepository,
                                     ApartmentRepository apartmentRepository,
                                     ResidentRepository residentRepository) {
        this.societyRepository = societyRepository;
        this.towerRepository = towerRepository;
        this.apartmentRepository = apartmentRepository;
        this.residentRepository = residentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TowerDto> getTowersBySociety(Long societyId) {
        if (!societyRepository.existsById(societyId)) {
            throw new ResourceNotFoundException("Society not found with id: " + societyId);
        }

        List<Tower> towers = towerRepository.findBySocietyId(societyId);
        return towers.stream().map(this::mapToTowerDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TowerDto getTowerById(Long towerId) {
        Tower tower = towerRepository.findById(towerId)
                .orElseThrow(() -> new ResourceNotFoundException("Tower not found with id: " + towerId));
        return mapToTowerDto(tower);
    }

    @Override
    public TowerDto createTower(Long societyId, CreateTowerRequest request) {
        Society society = societyRepository.findById(societyId)
                .orElseThrow(() -> new ResourceNotFoundException("Society not found with id: " + societyId));

        String trimmedName = request.getName().trim();
        if (towerRepository.existsBySocietyIdAndName(societyId, trimmedName)) {
            throw new IllegalArgumentException("Tower with name '" + trimmedName + "' already exists in this society");
        }

        Tower tower = new Tower();
        tower.setSociety(society);
        tower.setName(trimmedName);
        tower.setTotalFloors(request.getTotalFloors() != null ? request.getTotalFloors() : 20);

        Tower saved = towerRepository.save(tower);
        return mapToTowerDto(saved);
    }

    @Override
    public void deleteTower(Long towerId) {
        if (!towerRepository.existsById(towerId)) {
            throw new ResourceNotFoundException("Tower not found with id: " + towerId);
        }
        towerRepository.deleteById(towerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApartmentDto> getApartmentsByTower(Long towerId) {
        if (!towerRepository.existsById(towerId)) {
            throw new ResourceNotFoundException("Tower not found with id: " + towerId);
        }

        List<Apartment> apartments = apartmentRepository.findByTowerId(towerId);
        return apartments.stream().map(this::mapToApartmentDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApartmentDto> getApartmentsBySociety(Long societyId) {
        if (!societyRepository.existsById(societyId)) {
            throw new ResourceNotFoundException("Society not found with id: " + societyId);
        }

        List<Apartment> apartments = apartmentRepository.findByTowerSocietyId(societyId);
        return apartments.stream().map(this::mapToApartmentDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ApartmentDto getApartmentById(Long apartmentId) {
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Apartment not found with id: " + apartmentId));
        return mapToApartmentDto(apartment);
    }

    @Override
    public ApartmentDto createApartment(Long towerId, CreateApartmentRequest request) {
        Tower tower = towerRepository.findById(towerId)
                .orElseThrow(() -> new ResourceNotFoundException("Tower not found with id: " + towerId));

        String trimmedFlatNumber = request.getFlatNumber().trim();
        if (apartmentRepository.existsByTowerIdAndFlatNumber(towerId, trimmedFlatNumber)) {
            throw new IllegalArgumentException("Apartment flat '" + trimmedFlatNumber + "' already exists in this tower");
        }

        Apartment apartment = new Apartment();
        apartment.setTower(tower);
        apartment.setFlatNumber(trimmedFlatNumber);
        apartment.setFloorNumber(request.getFloorNumber());
        apartment.setConfiguration(request.getConfiguration() != null ? request.getConfiguration().trim() : "2BHK");
        apartment.setStatus(request.getStatus() != null ? request.getStatus().trim() : "OCCUPIED");

        Apartment saved = apartmentRepository.save(apartment);
        return mapToApartmentDto(saved);
    }

    @Override
    public ApartmentDto updateApartmentStatus(Long apartmentId, String status) {
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Apartment not found with id: " + apartmentId));

        apartment.setStatus(status.trim().toUpperCase());
        Apartment updated = apartmentRepository.save(apartment);
        return mapToApartmentDto(updated);
    }

    private TowerDto mapToTowerDto(Tower tower) {
        TowerDto dto = new TowerDto();
        dto.setId(tower.getId());
        dto.setSocietyId(tower.getSociety().getId());
        dto.setSocietyName(tower.getSociety().getName());
        dto.setName(tower.getName());
        dto.setTotalFloors(tower.getTotalFloors());
        dto.setTotalApartments(apartmentRepository.findByTowerId(tower.getId()).size());
        return dto;
    }

    private ApartmentDto mapToApartmentDto(Apartment apartment) {
        ApartmentDto dto = new ApartmentDto();
        dto.setId(apartment.getId());
        dto.setTowerId(apartment.getTower().getId());
        dto.setTowerName(apartment.getTower().getName());
        dto.setFlatNumber(apartment.getFlatNumber());
        dto.setFloorNumber(apartment.getFloorNumber());
        dto.setConfiguration(apartment.getConfiguration());
        dto.setStatus(apartment.getStatus());

        Optional<Resident> residentOpt = residentRepository.findByApartmentId(apartment.getId());
        residentOpt.ifPresent(r -> dto.setResidentName(r.getUser().getFullName()));

        return dto;
    }
}

