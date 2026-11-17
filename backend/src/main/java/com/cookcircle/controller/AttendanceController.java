package com.cookcircle.controller;

import com.cookcircle.dto.ApiResponse;
import com.cookcircle.dto.attendance.AttendanceDto;
import com.cookcircle.dto.attendance.LogAttendanceRequest;
import com.cookcircle.security.UserPrincipal;
import com.cookcircle.service.AttendanceService;
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
@RequestMapping("/api/v1/attendance")
@Tag(name = "Attendance", description = "Cook Daily Attendance, Check-In/Out and Punctuality Tracking")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/log")
    @PreAuthorize("hasRole('COOK')")
    @Operation(summary = "Log cook check-in / check-out attendance for a booking")
    public ResponseEntity<ApiResponse<AttendanceDto>> logAttendance(
            @Valid @RequestBody LogAttendanceRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        AttendanceDto attendance = attendanceService.logAttendance(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Attendance logged successfully", attendance));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('COOK')")
    @Operation(summary = "Get attendance history for logged-in cook partner")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> getMyAttendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<AttendanceDto> list = attendanceService.getCookAttendance(currentUser.getId(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/society/{societyId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Get daily attendance roster for all cooks in a society")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> getSocietyAttendance(
            @PathVariable Long societyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AttendanceDto> list = attendanceService.getDailySocietyAttendance(societyId, date);
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}

