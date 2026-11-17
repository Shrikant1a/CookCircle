package com.cookcircle.controller;

import com.cookcircle.dto.ApiResponse;
import com.cookcircle.dto.slot.CookingSlotDto;
import com.cookcircle.dto.slot.CreateCookingSlotRequest;
import com.cookcircle.dto.slot.SlotAvailabilityDto;
import com.cookcircle.service.CookingSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Cooking Slots", description = "45-Minute Cooking Slot Management and Tower Schedule Grid")
public class CookingSlotController {

    private final CookingSlotService cookingSlotService;

    public CookingSlotController(CookingSlotService cookingSlotService) {
        this.cookingSlotService = cookingSlotService;
    }

    @GetMapping("/towers/{towerId}/slots")
    @Operation(summary = "Get configured cooking slots for a tower block")
    public ResponseEntity<ApiResponse<List<CookingSlotDto>>> getSlots(
            @PathVariable Long towerId,
            @RequestParam(required = false) String shift) {
        List<CookingSlotDto> slots = cookingSlotService.getSlotsByTower(towerId, shift);
        return ResponseEntity.ok(ApiResponse.success(slots));
    }

    @PostMapping("/towers/{towerId}/slots")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Create a custom cooking slot in a tower block")
    public ResponseEntity<ApiResponse<CookingSlotDto>> createSlot(
            @PathVariable Long towerId,
            @Valid @RequestBody CreateCookingSlotRequest request) {
        CookingSlotDto slot = cookingSlotService.createSlot(towerId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cooking slot created successfully", slot));
    }

    @PostMapping("/towers/{towerId}/slots/initialize")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Initialize default 5-slot morning (07:00-10:45) and evening (18:00-21:45) schedules")
    public ResponseEntity<ApiResponse<Void>> initializeDefaultSlots(@PathVariable Long towerId) {
        cookingSlotService.initializeDefaultSlotsForTower(towerId);
        return ResponseEntity.ok(ApiResponse.success("Default cooking slots initialized for tower", null));
    }

    @GetMapping("/towers/{towerId}/slots/availability")
    @Operation(summary = "Get slot availability grid for a date and shift")
    public ResponseEntity<ApiResponse<List<SlotAvailabilityDto>>> getAvailability(
            @PathVariable Long towerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String shift) {
        List<SlotAvailabilityDto> availability = cookingSlotService.getAvailableSlotsForDate(towerId, date, shift);
        return ResponseEntity.ok(ApiResponse.success(availability));
    }
}

