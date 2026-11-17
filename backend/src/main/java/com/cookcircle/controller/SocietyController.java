package com.cookcircle.controller;

import com.cookcircle.dto.ApiResponse;
import com.cookcircle.dto.society.CreateSocietyRequest;
import com.cookcircle.dto.society.SocietyDto;
import com.cookcircle.exception.UnauthorizedException;
import com.cookcircle.security.UserPrincipal;
import com.cookcircle.service.SocietyService;
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
@RequestMapping("/api/v1/societies")
@Tag(name = "Societies", description = "Society Management APIs")
public class SocietyController {

    private final SocietyService societyService;

    public SocietyController(SocietyService societyService) {
        this.societyService = societyService;
    }

    @GetMapping
    @Operation(summary = "Get all registered societies")
    public ResponseEntity<ApiResponse<List<SocietyDto>>> getAllSocieties() {
        List<SocietyDto> list = societyService.getAllSocieties();
        return ResponseEntity.ok(ApiResponse.success("Societies retrieved successfully", list));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get society by ID")
    public ResponseEntity<ApiResponse<SocietyDto>> getSocietyById(@PathVariable Long id) {
        SocietyDto dto = societyService.getSocietyById(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get society by code")
    public ResponseEntity<ApiResponse<SocietyDto>> getSocietyByCode(@PathVariable String code) {
        SocietyDto dto = societyService.getSocietyByCode(code);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Create a new society (Super Admin only)")
    public ResponseEntity<ApiResponse<SocietyDto>> createSociety(@Valid @RequestBody CreateSocietyRequest request) {
        SocietyDto created = societyService.createSociety(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Society created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Update society details (Super Admin or Society Admin of this society)")
    public ResponseEntity<ApiResponse<SocietyDto>> updateSociety(
            @PathVariable Long id,
            @Valid @RequestBody CreateSocietyRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        if (currentUser.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"))) {
            if (currentUser.getSocietyId() == null || !currentUser.getSocietyId().equals(id)) {
                throw new UnauthorizedException("You are not authorized to update details of another society");
            }
        }

        SocietyDto updated = societyService.updateSociety(id, request);
        return ResponseEntity.ok(ApiResponse.success("Society updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Delete society (Super Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteSociety(@PathVariable Long id) {
        societyService.deleteSociety(id);
        return ResponseEntity.ok(ApiResponse.success("Society deleted successfully", null));
    }
}

