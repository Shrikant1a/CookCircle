package com.cookcircle.controller;

import com.cookcircle.dto.ApiResponse;
import com.cookcircle.dto.apartment.ApartmentDto;
import com.cookcircle.dto.apartment.CreateApartmentRequest;
import com.cookcircle.dto.society.TowerDto;
import com.cookcircle.exception.UnauthorizedException;
import com.cookcircle.security.UserPrincipal;
import com.cookcircle.service.TowerApartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Apartments", description = "Apartment and Flat Management APIs")
public class ApartmentController {

    private final TowerApartmentService towerApartmentService;

    public ApartmentController(TowerApartmentService towerApartmentService) {
        this.towerApartmentService = towerApartmentService;
    }

    @GetMapping("/towers/{towerId}/apartments")
    @Operation(summary = "Get all apartments for a specific tower")
    public ResponseEntity<ApiResponse<List<ApartmentDto>>> getApartmentsByTower(@PathVariable Long towerId) {
        List<ApartmentDto> list = towerApartmentService.getApartmentsByTower(towerId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/societies/{societyId}/apartments")
    @Operation(summary = "Get all apartments across all towers for a society")
    public ResponseEntity<ApiResponse<List<ApartmentDto>>> getApartmentsBySociety(@PathVariable Long societyId) {
        List<ApartmentDto> list = towerApartmentService.getApartmentsBySociety(societyId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/apartments/{id}")
    @Operation(summary = "Get apartment details by ID")
    public ResponseEntity<ApiResponse<ApartmentDto>> getApartmentById(@PathVariable Long id) {
        ApartmentDto dto = towerApartmentService.getApartmentById(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PostMapping("/towers/{towerId}/apartments")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Add a new flat/apartment to a tower")
    public ResponseEntity<ApiResponse<ApartmentDto>> createApartment(
            @PathVariable Long towerId,
            @Valid @RequestBody CreateApartmentRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        TowerDto tower = towerApartmentService.getTowerById(towerId);
        if (currentUser.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"))) {
            if (currentUser.getSocietyId() == null || !currentUser.getSocietyId().equals(tower.getSocietyId())) {
                throw new UnauthorizedException("You are not authorized to add apartments to another society");
            }
        }

        ApartmentDto created = towerApartmentService.createApartment(towerId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Apartment created successfully", created));
    }

    @PatchMapping("/apartments/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Update apartment occupancy status (OCCUPIED / VACANT)")
    public ResponseEntity<ApiResponse<ApartmentDto>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        String status = body.get("status");
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status is required");
        }

        ApartmentDto apartment = towerApartmentService.getApartmentById(id);
        TowerDto tower = towerApartmentService.getTowerById(apartment.getTowerId());
        if (currentUser.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"))) {
            if (currentUser.getSocietyId() == null || !currentUser.getSocietyId().equals(tower.getSocietyId())) {
                throw new UnauthorizedException("You are not authorized to modify apartments in another society");
            }
        }

        ApartmentDto updated = towerApartmentService.updateApartmentStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Status updated successfully", updated));
    }
}

