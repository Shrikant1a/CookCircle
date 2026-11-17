package com.cookcircle.controller;

import com.cookcircle.dto.ApiResponse;
import com.cookcircle.dto.leave.ApplyLeaveRequest;
import com.cookcircle.dto.leave.BackupAssignmentDto;
import com.cookcircle.dto.leave.LeaveRequestDto;
import com.cookcircle.dto.leave.ReviewLeaveRequest;
import com.cookcircle.security.UserPrincipal;
import com.cookcircle.service.LeaveAndBackupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/leaves")
@Tag(name = "Leave & Backup Cooks", description = "Cook Leave Requests, Approvals and Standby Float Cook Auto-Dispatch")
public class LeaveController {

    private final LeaveAndBackupService leaveAndBackupService;

    public LeaveController(LeaveAndBackupService leaveAndBackupService) {
        this.leaveAndBackupService = leaveAndBackupService;
    }

    @PostMapping
    @PreAuthorize("hasRole('COOK')")
    @Operation(summary = "Cook submits a leave request")
    public ResponseEntity<ApiResponse<LeaveRequestDto>> applyLeave(
            @Valid @RequestBody ApplyLeaveRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        LeaveRequestDto leave = leaveAndBackupService.applyLeave(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Leave request submitted successfully", leave));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('COOK')")
    @Operation(summary = "Get leave history for logged-in cook partner")
    public ResponseEntity<ApiResponse<List<LeaveRequestDto>>> getMyLeaves(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<LeaveRequestDto> list = leaveAndBackupService.getCookLeaves(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Get leave requests for a society (with optional status filter: PENDING, APPROVED, REJECTED)")
    public ResponseEntity<ApiResponse<List<LeaveRequestDto>>> getSocietyLeaves(
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

        List<LeaveRequestDto> list = leaveAndBackupService.getSocietyLeaves(targetSocietyId, status);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Approve cook leave, reassign affected apartment bookings to standby float cook, and dispatch backup")
    public ResponseEntity<ApiResponse<LeaveRequestDto>> approveLeave(
            @PathVariable Long id,
            @RequestBody(required = false) ReviewLeaveRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        LeaveRequestDto approved = leaveAndBackupService.approveLeave(currentUser.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Leave approved. Backup cook dispatched with zero meal disruption.", approved));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Reject cook leave request")
    public ResponseEntity<ApiResponse<LeaveRequestDto>> rejectLeave(
            @PathVariable Long id,
            @RequestBody(required = false) ReviewLeaveRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        LeaveRequestDto rejected = leaveAndBackupService.rejectLeave(currentUser.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Leave request rejected", rejected));
    }

    @GetMapping("/backup-assignments")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN', 'RESIDENT', 'COOK')")
    @Operation(summary = "Get dispatched backup float cook assignments for a date")
    public ResponseEntity<ApiResponse<List<BackupAssignmentDto>>> getBackupAssignments(
            @RequestParam(required = false) Long societyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long targetSocietyId = societyId != null ? societyId : (currentUser.getSocietyId() != null ? currentUser.getSocietyId() : 1L);
        List<BackupAssignmentDto> list = leaveAndBackupService.getBackupAssignments(targetSocietyId, date);
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}

