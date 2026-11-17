package com.cookcircle.controller;

import com.cookcircle.dto.ApiResponse;
import com.cookcircle.dto.society.CreateTowerRequest;
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

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Towers", description = "Tower and Block Management APIs")
public class TowerController {

    private final TowerApartmentService towerApartmentService;

    public TowerController(TowerApartmentService towerApartmentService) {
        this.towerApartmentService = towerApartmentService;
    }

    @GetMapping("/societies/{societyId}/towers")
    @Operation(summary = "Get all towers belonging to a society")
    public ResponseEntity<ApiResponse<List<TowerDto>>> getTowersBySociety(@PathVariable Long societyId) {
        List<TowerDto> towers = towerApartmentService.getTowersBySociety(societyId);
        return ResponseEntity.ok(ApiResponse.success(towers));
    }

    @GetMapping("/towers/{id}")
    @Operation(summary = "Get tower details by ID")
    public ResponseEntity<ApiResponse<TowerDto>> getTowerById(@PathVariable Long id) {
        TowerDto tower = towerApartmentService.getTowerById(id);
        return ResponseEntity.ok(ApiResponse.success(tower));
    }

    @PostMapping("/societies/{societyId}/towers")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Add a new tower to a society")
    public ResponseEntity<ApiResponse<TowerDto>> createTower(
            @PathVariable Long societyId,
            @Valid @RequestBody CreateTowerRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        if (currentUser.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"))) {
            if (currentUser.getSocietyId() == null || !currentUser.getSocietyId().equals(societyId)) {
                throw new UnauthorizedException("You are not authorized to create towers for another society");
            }
        }

        TowerDto created = towerApartmentService.createTower(societyId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tower added successfully", created));
    }

    @DeleteMapping("/towers/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Delete a tower")
    public ResponseEntity<ApiResponse<Void>> deleteTower(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        TowerDto tower = towerApartmentService.getTowerById(id);
        if (currentUser.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"))) {
            if (currentUser.getSocietyId() == null || !currentUser.getSocietyId().equals(tower.getSocietyId())) {
                throw new UnauthorizedException("You are not authorized to delete towers in another society");
            }
        }

        towerApartmentService.deleteTower(id);
        return ResponseEntity.ok(ApiResponse.success("Tower deleted successfully", null));
    }
}

