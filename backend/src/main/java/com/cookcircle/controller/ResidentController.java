package com.cookcircle.controller;

import com.cookcircle.dto.ApiResponse;
import com.cookcircle.dto.resident.ResidentDto;
import com.cookcircle.dto.resident.UpdateResidentPreferencesRequest;
import com.cookcircle.exception.UnauthorizedException;
import com.cookcircle.security.UserPrincipal;
import com.cookcircle.service.ResidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/residents")
@Tag(name = "Residents", description = "Resident Management and Profiles")
public class ResidentController {

    private final ResidentService residentService;

    public ResidentController(ResidentService residentService) {
        this.residentService = residentService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Get residents of society (Admin only)")
    public ResponseEntity<ApiResponse<List<ResidentDto>>> getResidents(
            @RequestParam(required = false) Long societyId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long targetSocietyId = societyId;
        boolean isSuperAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (!isSuperAdmin) {
            targetSocietyId = currentUser.getSocietyId();
        } else if (targetSocietyId == null) {
            targetSocietyId = currentUser.getSocietyId() != null ? currentUser.getSocietyId() : 1L;
        }

        List<ResidentDto> list = residentService.getResidentsBySociety(targetSocietyId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('RESIDENT')")
    @Operation(summary = "Get current resident's profile and apartment details")
    public ResponseEntity<ApiResponse<ResidentDto>> getCurrentResidentProfile(@AuthenticationPrincipal UserPrincipal currentUser) {
        ResidentDto resident = residentService.getResidentByUserId(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(resident));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN', 'RESIDENT')")
    @Operation(summary = "Get resident profile by ID")
    public ResponseEntity<ApiResponse<ResidentDto>> getResidentById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        ResidentDto resident = residentService.getResidentById(id);
        boolean isSuperAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (!isSuperAdmin) {
            if (currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_RESIDENT"))) {
                if (!resident.getUserId().equals(currentUser.getId())) {
                    throw new UnauthorizedException("You are not authorized to view another resident's profile");
                }
            } else if (currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SOCIETY_ADMIN"))) {
                if (currentUser.getSocietyId() == null || !currentUser.getSocietyId().equals(resident.getSocietyId())) {
                    throw new UnauthorizedException("You are not authorized to view residents of another society");
                }
            }
        }

        return ResponseEntity.ok(ApiResponse.success(resident));
    }

    @PutMapping("/{id}/preferences")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN', 'RESIDENT')")
    @Operation(summary = "Update resident preferences (Dietary preference, household size, contact phone)")
    public ResponseEntity<ApiResponse<ResidentDto>> updatePreferences(
            @PathVariable Long id,
            @RequestBody UpdateResidentPreferencesRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        ResidentDto resident = residentService.getResidentById(id);
        boolean isSuperAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (!isSuperAdmin) {
            if (currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_RESIDENT"))) {
                if (!resident.getUserId().equals(currentUser.getId())) {
                    throw new UnauthorizedException("You are not authorized to edit another resident's profile");
                }
            } else if (currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SOCIETY_ADMIN"))) {
                if (currentUser.getSocietyId() == null || !currentUser.getSocietyId().equals(resident.getSocietyId())) {
                    throw new UnauthorizedException("You are not authorized to edit residents of another society");
                }
            }
        }

        ResidentDto updated = residentService.updatePreferences(id, request);
        return ResponseEntity.ok(ApiResponse.success("Preferences updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Activate or deactivate resident account")
    public ResponseEntity<ApiResponse<ResidentDto>> updateResidentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        String status = body.get("status");
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status is required");
        }

        ResidentDto resident = residentService.getResidentById(id);
        boolean isSuperAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (!isSuperAdmin) {
            if (currentUser.getSocietyId() == null || !currentUser.getSocietyId().equals(resident.getSocietyId())) {
                throw new UnauthorizedException("You are not authorized to change status of residents in another society");
            }
        }

        ResidentDto updated = residentService.updateResidentStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Resident status updated successfully", updated));
    }
}

