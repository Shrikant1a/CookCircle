package com.cookcircle.service;

import com.cookcircle.dto.report.SocietyDashboardSummaryDto;

import java.time.LocalDate;

public interface ReportService {
    SocietyDashboardSummaryDto getSocietyDashboardSummary(Long societyId);
    String generateInvoicesCsv(Long societyId, String billingMonth);
    String generateAttendanceCsv(Long societyId, LocalDate date);
}

