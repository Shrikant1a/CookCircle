package com.cookcircle.service.impl;

import com.cookcircle.dto.cook.AssignCookRequest;
import com.cookcircle.dto.cook.CookAssignmentDto;
import com.cookcircle.dto.cook.CookDto;
import com.cookcircle.dto.cook.CreateCookRequest;
import com.cookcircle.dto.cook.UpdateCookProfileRequest;
import com.cookcircle.entity.*;
import com.cookcircle.exception.ResourceNotFoundException;
import com.cookcircle.repository.*;
import com.cookcircle.service.CookService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CookServiceImpl implements CookService {

    private final CookRepository cookRepository;
    private final CookAssignmentRepository cookAssignmentRepository;
    private final SocietyRepository societyRepository;
    private final TowerRepository towerRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public CookServiceImpl(CookRepository cookRepository,
                           CookAssignmentRepository cookAssignmentRepository,
                           SocietyRepository societyRepository,
                           TowerRepository towerRepository,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.cookRepository = cookRepository;
        this.cookAssignmentRepository = cookAssignmentRepository;
        this.societyRepository = societyRepository;
        this.towerRepository = towerRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CookDto> getCooksBySociety(Long societyId, String status, String type) {
        if (!societyRepository.existsById(societyId)) {
            throw new ResourceNotFoundException("Society not found with id: " + societyId);
        }

        List<Cook> cooks;
        if (status != null && !status.isBlank() && type != null && !type.isBlank()) {
            cooks = cookRepository.findBySocietyIdAndTypeAndStatus(societyId, type.trim().toUpperCase(), status.trim().toUpperCase());
        } else if (status != null && !status.isBlank()) {
            cooks = cookRepository.findBySocietyIdAndStatus(societyId, status.trim().toUpperCase());
        } else if (type != null && !type.isBlank()) {
            cooks = cookRepository.findBySocietyIdAndType(societyId, type.trim().toUpperCase());
        } else {
            cooks = cookRepository.findBySocietyId(societyId);
        }

        return cooks.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CookDto getCookById(Long id) {
        Cook cook = cookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cook not found with id: " + id));
        return mapToDto(cook);
    }

    @Override
    @Transactional(readOnly = true)
    public CookDto getCookByUserId(Long userId) {
        Cook cook = cookRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cook record not found for user id: " + userId));
        return mapToDto(cook);
    }

    @Override
    public CookDto createCook(Long societyId, CreateCookRequest request) {
        Society society = societyRepository.findById(societyId)
                .orElseThrow(() -> new ResourceNotFoundException("Society not found with id: " + societyId));

        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email already exists: " + email);
        }

        String phone = request.getPhone().trim();
        if (userRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("User with phone already exists: " + phone);
        }

        Role cookRole = roleRepository.findByName("ROLE_COOK")
                .or(() -> roleRepository.findByName("COOK"))
                .orElseGet(() -> roleRepository.save(new Role("ROLE_COOK", "Society cook partner")));

        User user = new User();
        user.setEmail(email);
        user.setPhone(phone);
        user.setFullName(request.getFullName().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(cookRole);
        user.setStatus("ACTIVE");
        User savedUser = userRepository.save(user);

        Cook cook = new Cook();
        cook.setUser(savedUser);
        cook.setSociety(society);
        cook.setType(request.getType() != null ? request.getType().trim().toUpperCase() : "PRIMARY_COOK");
        cook.setSpecialties(request.getSpecialties() != null ? request.getSpecialties().trim() : "North Indian, Jain");
        cook.setExperienceYears(request.getExperienceYears() != null ? request.getExperienceYears() : 5);
        cook.setAadhaarVerified(request.getAadhaarVerified() != null ? request.getAadhaarVerified() : true);
        cook.setPoliceVerified(request.getPoliceVerified() != null ? request.getPoliceVerified() : true);
        cook.setVaccinationStatus(request.getVaccinationStatus() != null ? request.getVaccinationStatus().trim() : "Double Vaccinated");
        cook.setStatus("ACTIVE");

        Cook savedCook = cookRepository.save(cook);
        return mapToDto(savedCook);
    }

    @Override
    public CookDto updateCookProfile(Long id, UpdateCookProfileRequest request) {
        Cook cook = cookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cook not found with id: " + id));

        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            cook.getUser().setPhone(request.getPhone().trim());
            userRepository.save(cook.getUser());
        }
        if (request.getSpecialties() != null && !request.getSpecialties().isBlank()) {
            cook.setSpecialties(request.getSpecialties().trim());
        }
        if (request.getExperienceYears() != null) {
            cook.setExperienceYears(request.getExperienceYears());
        }
        if (request.getVaccinationStatus() != null && !request.getVaccinationStatus().isBlank()) {
            cook.setVaccinationStatus(request.getVaccinationStatus().trim());
        }

        Cook updated = cookRepository.save(cook);
        return mapToDto(updated);
    }

    @Override
    public CookDto updateCookVerification(Long id, Boolean policeVerified, Boolean aadhaarVerified, String vaccinationStatus) {
        Cook cook = cookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cook not found with id: " + id));

        if (policeVerified != null) {
            cook.setPoliceVerified(policeVerified);
        }
        if (aadhaarVerified != null) {
            cook.setAadhaarVerified(aadhaarVerified);
        }
        if (vaccinationStatus != null && !vaccinationStatus.isBlank()) {
            cook.setVaccinationStatus(vaccinationStatus.trim());
        }

        Cook updated = cookRepository.save(cook);
        return mapToDto(updated);
    }

    @Override
    public CookDto updateCookStatus(Long id, String status) {
        Cook cook = cookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cook not found with id: " + id));

        cook.setStatus(status.trim().toUpperCase());
        cook.getUser().setStatus(status.trim().toUpperCase());
        userRepository.save(cook.getUser());
        Cook updated = cookRepository.save(cook);
        return mapToDto(updated);
    }

    @Override
    public CookAssignmentDto assignCookToTower(Long cookId, AssignCookRequest request) {
        Cook cook = cookRepository.findById(cookId)
                .orElseThrow(() -> new ResourceNotFoundException("Cook not found with id: " + cookId));

        if (!"ACTIVE".equalsIgnoreCase(cook.getStatus())) {
            throw new IllegalArgumentException("Cannot assign an inactive or on-leave cook to a tower");
        }

        Tower tower = towerRepository.findById(request.getTowerId())
                .orElseThrow(() -> new ResourceNotFoundException("Tower not found with id: " + request.getTowerId()));

        if (!tower.getSociety().getId().equals(cook.getSociety().getId())) {
            throw new IllegalArgumentException("Cross-society assignment denied: cook and tower belong to different societies");
        }

        String shift = request.getShift() != null ? request.getShift().trim().toUpperCase() : "MORNING";
        if (cookAssignmentRepository.existsByCookIdAndTowerIdAndShiftAndStatus(cookId, request.getTowerId(), shift, "ACTIVE")) {
            throw new IllegalArgumentException("Cook is already assigned to " + tower.getName() + " for shift " + shift);
        }

        CookAssignment assignment = new CookAssignment();
        assignment.setCook(cook);
        assignment.setTower(tower);
        assignment.setShift(shift);
        assignment.setStartDate(request.getStartDate() != null ? request.getStartDate() : LocalDate.now());
        assignment.setEndDate(request.getEndDate());
        assignment.setStatus("ACTIVE");

        CookAssignment saved = cookAssignmentRepository.save(assignment);
        return mapToAssignmentDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CookAssignmentDto> getCookAssignments(Long cookId) {
        if (!cookRepository.existsById(cookId)) {
            throw new ResourceNotFoundException("Cook not found with id: " + cookId);
        }

        return cookAssignmentRepository.findByCookId(cookId).stream()
                .map(this::mapToAssignmentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CookAssignmentDto> getTowerCookAssignments(Long towerId) {
        if (!towerRepository.existsById(towerId)) {
            throw new ResourceNotFoundException("Tower not found with id: " + towerId);
        }

        return cookAssignmentRepository.findByTowerId(towerId).stream()
                .map(this::mapToAssignmentDto)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelCookAssignment(Long assignmentId) {
        CookAssignment assignment = cookAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Cook assignment not found with id: " + assignmentId));

        assignment.setStatus("CANCELLED");
        assignment.setEndDate(LocalDate.now());
        cookAssignmentRepository.save(assignment);
    }

    private CookDto mapToDto(Cook cook) {
        CookDto dto = new CookDto();
        dto.setId(cook.getId());
        dto.setUserId(cook.getUser().getId());
        dto.setFullName(cook.getUser().getFullName());
        dto.setEmail(cook.getUser().getEmail());
        dto.setPhone(cook.getUser().getPhone());

        dto.setSocietyId(cook.getSociety().getId());
        dto.setSocietyName(cook.getSociety().getName());

        dto.setType(cook.getType());
        dto.setRating(cook.getRating());
        dto.setSpecialties(cook.getSpecialties());
        dto.setExperienceYears(cook.getExperienceYears());
        dto.setAadhaarVerified(cook.getAadhaarVerified());
        dto.setPoliceVerified(cook.getPoliceVerified());
        dto.setVaccinationStatus(cook.getVaccinationStatus());
        dto.setStatus(cook.getStatus());
        dto.setCreatedAt(cook.getCreatedAt());

        List<CookAssignment> activeAssignments = cookAssignmentRepository.findByCookIdAndStatus(cook.getId(), "ACTIVE");
        List<String> towers = activeAssignments.stream()
                .map(a -> a.getTower().getName() + " (" + a.getShift() + ")")
                .distinct()
                .collect(Collectors.toList());
        dto.setAssignedTowers(towers);

        return dto;
    }

    private CookAssignmentDto mapToAssignmentDto(CookAssignment a) {
        CookAssignmentDto dto = new CookAssignmentDto();
        dto.setId(a.getId());
        dto.setCookId(a.getCook().getId());
        dto.setCookName(a.getCook().getUser().getFullName());
        dto.setTowerId(a.getTower().getId());
        dto.setTowerName(a.getTower().getName());
        dto.setSocietyId(a.getTower().getSociety().getId());
        dto.setSocietyName(a.getTower().getSociety().getName());
        dto.setShift(a.getShift());
        dto.setStartDate(a.getStartDate());
        dto.setEndDate(a.getEndDate());
        dto.setStatus(a.getStatus());
        dto.setCreatedAt(a.getCreatedAt());
        return dto;
    }
}

