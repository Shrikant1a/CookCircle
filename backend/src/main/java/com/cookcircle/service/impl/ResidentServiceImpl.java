package com.cookcircle.service.impl;

import com.cookcircle.dto.resident.ResidentDto;
import com.cookcircle.dto.resident.UpdateResidentPreferencesRequest;
import com.cookcircle.entity.Resident;
import com.cookcircle.exception.ResourceNotFoundException;
import com.cookcircle.repository.ResidentRepository;
import com.cookcircle.repository.SocietyRepository;
import com.cookcircle.repository.UserRepository;
import com.cookcircle.service.ResidentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResidentServiceImpl implements ResidentService {

    private final ResidentRepository residentRepository;
    private final SocietyRepository societyRepository;
    private final UserRepository userRepository;

    public ResidentServiceImpl(ResidentRepository residentRepository,
                               SocietyRepository societyRepository,
                               UserRepository userRepository) {
        this.residentRepository = residentRepository;
        this.societyRepository = societyRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResidentDto> getResidentsBySociety(Long societyId) {
        if (!societyRepository.existsById(societyId)) {
            throw new ResourceNotFoundException("Society not found with id: " + societyId);
        }

        return residentRepository.findBySocietyId(societyId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ResidentDto getResidentById(Long residentId) {
        Resident resident = residentRepository.findById(residentId)
                .orElseThrow(() -> new ResourceNotFoundException("Resident not found with id: " + residentId));
        return mapToDto(resident);
    }

    @Override
    @Transactional(readOnly = true)
    public ResidentDto getResidentByUserId(Long userId) {
        Resident resident = residentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resident record not found for user id: " + userId));
        return mapToDto(resident);
    }

    @Override
    public ResidentDto updatePreferences(Long residentId, UpdateResidentPreferencesRequest request) {
        Resident resident = residentRepository.findById(residentId)
                .orElseThrow(() -> new ResourceNotFoundException("Resident not found with id: " + residentId));

        if (request.getHouseholdSize() != null && !request.getHouseholdSize().isBlank()) {
            resident.setHouseholdSize(request.getHouseholdSize().trim());
        }
        if (request.getDietaryPref() != null && !request.getDietaryPref().isBlank()) {
            resident.setDietaryPref(request.getDietaryPref().trim());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            resident.getUser().setPhone(request.getPhone().trim());
            userRepository.save(resident.getUser());
        }

        Resident saved = residentRepository.save(resident);
        return mapToDto(saved);
    }

    @Override
    public ResidentDto updateResidentStatus(Long residentId, String status) {
        Resident resident = residentRepository.findById(residentId)
                .orElseThrow(() -> new ResourceNotFoundException("Resident not found with id: " + residentId));

        resident.getUser().setStatus(status.trim().toUpperCase());
        userRepository.save(resident.getUser());
        return mapToDto(resident);
    }

    private ResidentDto mapToDto(Resident resident) {
        ResidentDto dto = new ResidentDto();
        dto.setId(resident.getId());
        dto.setUserId(resident.getUser().getId());
        dto.setFullName(resident.getUser().getFullName());
        dto.setEmail(resident.getUser().getEmail());
        dto.setPhone(resident.getUser().getPhone());

        dto.setSocietyId(resident.getSociety().getId());
        dto.setSocietyName(resident.getSociety().getName());

        if (resident.getApartment() != null) {
            dto.setApartmentId(resident.getApartment().getId());
            dto.setFlatNumber(resident.getApartment().getFlatNumber());
            dto.setFloorNumber(resident.getApartment().getFloorNumber());
            dto.setApartmentConfig(resident.getApartment().getConfiguration());

            if (resident.getApartment().getTower() != null) {
                dto.setTowerId(resident.getApartment().getTower().getId());
                dto.setTowerName(resident.getApartment().getTower().getName());
            }
        }

        dto.setHouseholdSize(resident.getHouseholdSize());
        dto.setDietaryPref(resident.getDietaryPref());
        dto.setWalletBalance(resident.getWalletBalance());
        dto.setCreatedAt(resident.getCreatedAt());

        return dto;
    }
}

