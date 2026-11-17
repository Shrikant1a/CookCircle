package com.cookcircle.service.impl;

import com.cookcircle.dto.auth.*;
import com.cookcircle.entity.*;
import com.cookcircle.exception.ResourceNotFoundException;
import com.cookcircle.exception.SlotConflictException;
import com.cookcircle.repository.*;
import com.cookcircle.security.JwtTokenProvider;
import com.cookcircle.security.UserPrincipal;
import com.cookcircle.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SocietyRepository societyRepository;
    private final ApartmentRepository apartmentRepository;
    private final ResidentRepository residentRepository;
    private final CookRepository cookRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtTokenProvider tokenProvider,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           SocietyRepository societyRepository,
                           ApartmentRepository apartmentRepository,
                           ResidentRepository residentRepository,
                           CookRepository cookRepository,
                           PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.societyRepository = societyRepository;
        this.apartmentRepository = apartmentRepository;
        this.residentRepository = residentRepository;
        this.cookRepository = cookRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String token = tokenProvider.generateToken(userPrincipal);
        UserSummaryDto userSummary = buildUserSummary(userPrincipal);

        return new AuthResponse(token, userSummary);
    }

    @Override
    @Transactional
    public AuthResponse registerResident(RegisterResidentRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new SlotConflictException("Email is already registered: " + request.getEmail());
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new SlotConflictException("Phone number is already registered: " + request.getPhone());
        }

        Society society = societyRepository.findById(request.getSocietyId())
                .orElseThrow(() -> new ResourceNotFoundException("Society not found with id: " + request.getSocietyId()));

        Role role = roleRepository.findByName("ROLE_RESIDENT")
                .orElseThrow(() -> new ResourceNotFoundException("Role ROLE_RESIDENT not found"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setFullName(request.getFullName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setStatus("ACTIVE");
        user = userRepository.save(user);

        // Map or find apartment
        Apartment apartment = apartmentRepository.findByTowerIdAndFlatNumber(request.getTowerId(), request.getFlatNumber())
                .orElse(null);

        if (apartment == null) {
            apartment = new Apartment();
            Tower towerRef = new Tower();
            towerRef.setId(request.getTowerId());
            apartment.setTower(towerRef);
            apartment.setFlatNumber(request.getFlatNumber());
            apartment.setFloorNumber(1);
            apartment.setConfiguration("2BHK");
            apartment.setStatus("OCCUPIED");
            apartment = apartmentRepository.save(apartment);
        }

        Resident resident = new Resident();
        resident.setUser(user);
        resident.setSociety(society);
        resident.setApartment(apartment);
        resident.setHouseholdSize(request.getHouseholdSize());
        resident.setDietaryPref(request.getDietaryPref());
        resident.setWalletBalance(BigDecimal.ZERO);
        residentRepository.save(resident);

        UserPrincipal userPrincipal = UserPrincipal.create(user, society.getId());
        String token = tokenProvider.generateToken(userPrincipal);
        UserSummaryDto userSummary = buildUserSummary(userPrincipal);
        userSummary.setSocietyName(society.getName());
        userSummary.setFlatNumber(apartment.getFlatNumber());

        return new AuthResponse(token, userSummary);
    }

    @Override
    @Transactional
    public AuthResponse registerCook(RegisterCookRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new SlotConflictException("Email is already registered: " + request.getEmail());
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new SlotConflictException("Phone number is already registered: " + request.getPhone());
        }

        Society society = societyRepository.findById(request.getSocietyId())
                .orElseThrow(() -> new ResourceNotFoundException("Society not found with id: " + request.getSocietyId()));

        Role role = roleRepository.findByName("ROLE_COOK")
                .orElseThrow(() -> new ResourceNotFoundException("Role ROLE_COOK not found"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setFullName(request.getFullName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setStatus("ACTIVE");
        user = userRepository.save(user);

        Cook cook = new Cook();
        cook.setUser(user);
        cook.setSociety(society);
        cook.setType(request.getType() != null ? request.getType() : "PRIMARY_COOK");
        cook.setSpecialties(request.getSpecialties());
        cook.setExperienceYears(request.getExperienceYears() != null ? request.getExperienceYears() : 5);
        cook.setAadhaarVerified(true);
        cook.setPoliceVerified(true);
        cook.setVaccinationStatus("Double Vaccinated");
        cook.setStatus("ACTIVE");
        cookRepository.save(cook);

        UserPrincipal userPrincipal = UserPrincipal.create(user, society.getId());
        String token = tokenProvider.generateToken(userPrincipal);
        UserSummaryDto userSummary = buildUserSummary(userPrincipal);
        userSummary.setSocietyName(society.getName());

        return new AuthResponse(token, userSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public UserSummaryDto getCurrentUser(UserPrincipal userPrincipal) {
        UserSummaryDto summary = buildUserSummary(userPrincipal);

        if (userPrincipal.getSocietyId() != null) {
            societyRepository.findById(userPrincipal.getSocietyId())
                    .ifPresent(s -> summary.setSocietyName(s.getName()));
        }

        if (userPrincipal.getRoleName().contains("RESIDENT")) {
            residentRepository.findByUserId(userPrincipal.getId())
                    .ifPresent(r -> summary.setFlatNumber(r.getApartment().getFlatNumber()));
        }

        return summary;
    }

    private UserSummaryDto buildUserSummary(UserPrincipal userPrincipal) {
        UserSummaryDto dto = new UserSummaryDto();
        dto.setId(userPrincipal.getId());
        dto.setEmail(userPrincipal.getUsername());
        dto.setFullName(userPrincipal.getFullName());
        dto.setRole(userPrincipal.getRoleName());
        dto.setSocietyId(userPrincipal.getSocietyId());
        return dto;
    }
}

