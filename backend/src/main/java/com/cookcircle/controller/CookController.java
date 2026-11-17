package com.cookcircle.controller;

import com.cookcircle.dto.ApiResponse;
import com.cookcircle.dto.cook.AssignCookRequest;
import com.cookcircle.dto.cook.CookAssignmentDto;
import com.cookcircle.dto.cook.CookDto;
import com.cookcircle.dto.cook.CreateCookRequest;
import com.cookcircle.dto.cook.UpdateCookProfileRequest;
import com.cookcircle.exception.UnauthorizedException;
import com.cookcircle.security.UserPrincipal;
import com.cookcircle.service.CookService;
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
@Tag(name = "Cooks", description = "Cook Partners and Tower Assignment APIs")
public class CookController {

    private final CookService cookService;

    public CookController(CookService cookService) {
        this.cookService = cookService;
    }

    @GetMapping("/cooks")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN', 'RESIDENT', 'COOK')")
    @Operation(summary = "Get cooks by society with optional status/type filter")
    public ResponseEntity<ApiResponse<List<CookDto>>> getCooks(
            @RequestParam(required = false) Long societyId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long targetSocietyId = societyId;
        boolean isSuperAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (!isSuperAdmin) {
            targetSocietyId = currentUser.getSocietyId();
        } else if (targetSocietyId == null) {
            targetSocietyId = currentUser.getSocietyId() != null ? currentUser.getSocietyId() : 1L;
        }

        List<CookDto> list = cookService.getCooksBySociety(targetSocietyId, status, type);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/cooks/me")
    @PreAuthorize("hasRole('COOK')")
    @Operation(summary = "Get current authenticated cook partner profile")
    public ResponseEntity<ApiResponse<CookDto>> getCurrentCookProfile(@AuthenticationPrincipal UserPrincipal currentUser) {
        CookDto cook = cookService.getCookByUserId(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(cook));
    }

    @GetMapping("/cooks/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN', 'RESIDENT', 'COOK')")
    @Operation(summary = "Get cook profile by ID")
    public ResponseEntity<ApiResponse<CookDto>> getCookById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        CookDto cook = cookService.getCookById(id);
        boolean isSuperAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (!isSuperAdmin && currentUser.getSocietyId() != null) {
            if (!currentUser.getSocietyId().equals(cook.getSocietyId())) {
                throw new UnauthorizedException("Cannot access cook profile from another society");
            }
        }

        return ResponseEntity.ok(ApiResponse.success(cook));
    }

    @PostMapping("/societies/{societyId}/cooks")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Add and onboard a new cook partner to a society")
    public ResponseEntity<ApiResponse<CookDto>> createCook(
            @PathVariable Long societyId,
            @Valid @RequestBody CreateCookRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        boolean isSuperAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (!isSuperAdmin && (currentUser.getSocietyId() == null || !currentUser.getSocietyId().equals(societyId))) {
            throw new UnauthorizedException("You are not authorized to onboard cooks to another society");
        }

        CookDto created = cookService.createCook(societyId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cook partner onboarded successfully", created));
    }

    @PutMapping("/cooks/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN', 'COOK')")
    @Operation(summary = "Update cook profile details")
    public ResponseEntity<ApiResponse<CookDto>> updateCookProfile(
            @PathVariable Long id,
            @RequestBody UpdateCookProfileRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        CookDto cook = cookService.getCookById(id);
        boolean isSuperAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (!isSuperAdmin) {
            if (currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_COOK"))) {
                if (!cook.getUserId().equals(currentUser.getId())) {
                    throw new UnauthorizedException("You are not authorized to modify another cook's profile");
                }
            } else if (currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SOCIETY_ADMIN"))) {
                if (currentUser.getSocietyId() == null || !currentUser.getSocietyId().equals(cook.getSocietyId())) {
                    throw new UnauthorizedException("You are not authorized to modify cooks in another society");
                }
            }
        }

        CookDto updated = cookService.updateCookProfile(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cook profile updated successfully", updated));
    }

    @PatchMapping("/cooks/{id}/verification")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Update cook trust verification badges (Police, Aadhaar, Vaccination)")
    public ResponseEntity<ApiResponse<CookDto>> updateVerification(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        CookDto cook = cookService.getCookById(id);
        boolean isSuperAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (!isSuperAdmin && (currentUser.getSocietyId() == null || !currentUser.getSocietyId().equals(cook.getSocietyId()))) {
            throw new UnauthorizedException("You are not authorized to verify cooks in another society");
        }

        Boolean policeVerified = body.containsKey("policeVerified") ? (Boolean) body.get("policeVerified") : null;
        Boolean aadhaarVerified = body.containsKey("aadhaarVerified") ? (Boolean) body.get("aadhaarVerified") : null;
        String vaccinationStatus = body.containsKey("vaccinationStatus") ? (String) body.get("vaccinationStatus") : null;

        CookDto updated = cookService.updateCookVerification(id, policeVerified, aadhaarVerified, vaccinationStatus);
        return ResponseEntity.ok(ApiResponse.success("Trust badges updated successfully", updated));
    }

    @PatchMapping("/cooks/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Update cook operational status (ACTIVE, ON_LEAVE, INACTIVE)")
    public ResponseEntity<ApiResponse<CookDto>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        String status = body.get("status");
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status is required");
        }

        CookDto cook = cookService.getCookById(id);
        boolean isSuperAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (!isSuperAdmin && (currentUser.getSocietyId() == null || !currentUser.getSocietyId().equals(cook.getSocietyId()))) {
            throw new UnauthorizedException("You are not authorized to change status of cooks in another society");
        }

        CookDto updated = cookService.updateCookStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Status updated successfully", updated));
    }

    @PostMapping("/cooks/{id}/assignments")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Assign a cook partner to a tower block")
    public ResponseEntity<ApiResponse<CookAssignmentDto>> assignCookToTower(
            @PathVariable Long id,
            @Valid @RequestBody AssignCookRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        CookDto cook = cookService.getCookById(id);
        boolean isSuperAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (!isSuperAdmin && (currentUser.getSocietyId() == null || !currentUser.getSocietyId().equals(cook.getSocietyId()))) {
            throw new UnauthorizedException("You are not authorized to assign cooks in another society");
        }

        CookAssignmentDto assignment = cookService.assignCookToTower(id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cook assigned to tower successfully", assignment));
    }

    @GetMapping("/cooks/{id}/assignments")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN', 'RESIDENT', 'COOK')")
    @Operation(summary = "Get tower assignments for a cook partner")
    public ResponseEntity<ApiResponse<List<CookAssignmentDto>>> getCookAssignments(@PathVariable Long id) {
        List<CookAssignmentDto> assignments = cookService.getCookAssignments(id);
        return ResponseEntity.ok(ApiResponse.success(assignments));
    }

    @GetMapping("/towers/{towerId}/cook-assignments")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN', 'RESIDENT', 'COOK')")
    @Operation(summary = "Get assigned cooks for a tower block")
    public ResponseEntity<ApiResponse<List<CookAssignmentDto>>> getTowerCookAssignments(@PathVariable Long towerId) {
        List<CookAssignmentDto> assignments = cookService.getTowerCookAssignments(towerId);
        return ResponseEntity.ok(ApiResponse.success(assignments));
    }

    @DeleteMapping("/cook-assignments/{assignmentId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Cancel or end a cook tower assignment")
    public ResponseEntity<ApiResponse<Void>> cancelAssignment(@PathVariable Long assignmentId) {
        cookService.cancelCookAssignment(assignmentId);
        return ResponseEntity.ok(ApiResponse.success("Assignment cancelled successfully", null));
    }
}

