package com.cookcircle.controller;

import com.cookcircle.dto.ApiResponse;
import com.cookcircle.dto.complaint.ComplaintDto;
import com.cookcircle.dto.complaint.CreateComplaintRequest;
import com.cookcircle.dto.complaint.ResolveComplaintRequest;
import com.cookcircle.security.UserPrincipal;
import com.cookcircle.service.ComplaintService;
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
@RequestMapping("/api/v1/complaints")
@Tag(name = "Complaints & Feedback", description = "Resident Grievance Reporting and Admin Resolution")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping
    @PreAuthorize("hasRole('RESIDENT')")
    @Operation(summary = "Resident raises a service complaint (Punctuality, Taste, Hygiene, Billing)")
    public ResponseEntity<ApiResponse<ComplaintDto>> createComplaint(
            @Valid @RequestBody CreateComplaintRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        ComplaintDto complaint = complaintService.createComplaint(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Complaint submitted successfully", complaint));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('RESIDENT')")
    @Operation(summary = "Get complaints raised by the logged-in resident")
    public ResponseEntity<ApiResponse<List<ComplaintDto>>> getMyComplaints(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<ComplaintDto> list = complaintService.getResidentComplaints(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Get all complaints for a society (with optional status filter)")
    public ResponseEntity<ApiResponse<List<ComplaintDto>>> getSocietyComplaints(
            @RequestParam(required = false) Long societyId,
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long targetSocietyId = societyId;
        boolean isSuperAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (!isSuperAdmin) {
            targetSocietyId = currentUser.getSocietyId();
        } else if (targetSocietyId == null) {
            targetSocietyId = currentUser.getSocietyId() != null ? currentUser.getSocietyId() : 1L;
        }

        List<ComplaintDto> list = complaintService.getSocietyComplaints(targetSocietyId, status);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PatchMapping("/{id}/resolve")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Society admin resolves a resident complaint")
    public ResponseEntity<ApiResponse<ComplaintDto>> resolveComplaint(
            @PathVariable Long id,
            @Valid @RequestBody ResolveComplaintRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        ComplaintDto resolved = complaintService.resolveComplaint(currentUser.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Complaint resolved successfully", resolved));
    }
}

