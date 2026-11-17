package com.cookcircle.controller;

import com.cookcircle.dto.ApiResponse;
import com.cookcircle.dto.report.SocietyDashboardSummaryDto;
import com.cookcircle.security.UserPrincipal;
import com.cookcircle.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports & Analytics", description = "Operational KPIs, Financial Billing Summary and CSV Export")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/dashboard-kpis")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Get society dashboard operational summary (Total Flats, MRR, Cooks, SLA %)")
    public ResponseEntity<ApiResponse<SocietyDashboardSummaryDto>> getDashboardSummary(
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

        SocietyDashboardSummaryDto summary = reportService.getSocietyDashboardSummary(targetSocietyId);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/invoices/csv")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Export monthly billing and payment collection report as CSV")
    public ResponseEntity<byte[]> exportInvoicesCsv(
            @RequestParam(required = false) Long societyId,
            @RequestParam(required = false) String billingMonth,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long targetSocietyId = societyId != null ? societyId : (currentUser.getSocietyId() != null ? currentUser.getSocietyId() : 1L);
        String csv = reportService.generateInvoicesCsv(targetSocietyId, billingMonth);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cookcircle_invoices_" + (billingMonth != null ? billingMonth : "all") + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.getBytes());
    }

    @GetMapping("/attendance/csv")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Export cook daily attendance roster and punctuality as CSV")
    public ResponseEntity<byte[]> exportAttendanceCsv(
            @RequestParam(required = false) Long societyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long targetSocietyId = societyId != null ? societyId : (currentUser.getSocietyId() != null ? currentUser.getSocietyId() : 1L);
        LocalDate targetDate = date != null ? date : LocalDate.now();
        String csv = reportService.generateAttendanceCsv(targetSocietyId, targetDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cookcircle_attendance_" + targetDate + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.getBytes());
    }
}

