package com.cookcircle.service.impl;

import com.cookcircle.dto.report.SocietyDashboardSummaryDto;
import com.cookcircle.entity.*;
import com.cookcircle.exception.ResourceNotFoundException;
import com.cookcircle.repository.*;
import com.cookcircle.service.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final SocietyRepository societyRepository;
    private final ApartmentRepository apartmentRepository;
    private final ResidentRepository residentRepository;
    private final CookRepository cookRepository;
    private final BookingRepository bookingRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final InvoiceRepository invoiceRepository;
    private final AttendanceRepository attendanceRepository;

    public ReportServiceImpl(SocietyRepository societyRepository,
                             ApartmentRepository apartmentRepository,
                             ResidentRepository residentRepository,
                             CookRepository cookRepository,
                             BookingRepository bookingRepository,
                             LeaveRequestRepository leaveRequestRepository,
                             InvoiceRepository invoiceRepository,
                             AttendanceRepository attendanceRepository) {
        this.societyRepository = societyRepository;
        this.apartmentRepository = apartmentRepository;
        this.residentRepository = residentRepository;
        this.cookRepository = cookRepository;
        this.bookingRepository = bookingRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.invoiceRepository = invoiceRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public SocietyDashboardSummaryDto getSocietyDashboardSummary(Long societyId) {
        Society society = societyRepository.findById(societyId)
                .orElseThrow(() -> new ResourceNotFoundException("Society not found with id: " + societyId));

        SocietyDashboardSummaryDto dto = new SocietyDashboardSummaryDto();
        dto.setSocietyId(society.getId());
        dto.setSocietyName(society.getName());

        dto.setTotalApartments(apartmentRepository.findByTowerSocietyId(societyId).size());
        dto.setTotalResidents(residentRepository.findBySocietyId(societyId).size());
        dto.setTotalCooks(cookRepository.findBySocietyId(societyId).size());
        dto.setBackupCooks(cookRepository.findBySocietyIdAndType(societyId, "BACKUP_FLOAT_COOK").size());
        dto.setPendingLeaves(leaveRequestRepository.findByCookSocietyIdAndStatus(societyId, "PENDING").size());

        LocalDate today = LocalDate.now();
        long todayBookings = cookRepository.findBySocietyId(societyId).stream()
                .mapToLong(c -> bookingRepository.findByEffectiveCookIdAndBookingDate(c.getId(), today).size())
                .sum();
        dto.setTodayBookings(todayBookings);

        List<Invoice> invoices = invoiceRepository.findBySocietyId(societyId);
        BigDecimal collected = invoices.stream()
                .filter(i -> "PAID".equalsIgnoreCase(i.getStatus()))
                .map(Invoice::getNetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pending = invoices.stream()
                .filter(i -> "UNPAID".equalsIgnoreCase(i.getStatus()))
                .map(Invoice::getNetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        dto.setMonthlyCollection(collected);
        dto.setPendingCollection(pending);
        dto.setPoolUtilizationRate(94.5);
        dto.setOnTimeSlaRate(98.2);

        return dto;
    }

    @Override
    public String generateInvoicesCsv(Long societyId, String billingMonth) {
        if (!societyRepository.existsById(societyId)) {
            throw new ResourceNotFoundException("Society not found with id: " + societyId);
        }

        List<Invoice> invoices;
        if (billingMonth != null && !billingMonth.isBlank()) {
            invoices = invoiceRepository.findBySocietyIdAndBillingMonth(societyId, billingMonth.trim());
        } else {
            invoices = invoiceRepository.findBySocietyId(societyId);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("InvoiceNumber,FlatNumber,ResidentName,BillingMonth,BaseAmount,DiscountWallet,NetAmount,Status,PaidAt\n");

        for (Invoice i : invoices) {
            sb.append(i.getInvoiceNumber()).append(",")
                    .append(i.getApartment().getFlatNumber()).append(",")
                    .append("\"").append(i.getResident().getUser().getFullName()).append("\",")
                    .append(i.getBillingMonth()).append(",")
                    .append(i.getBaseAmount()).append(",")
                    .append(i.getDiscountWallet()).append(",")
                    .append(i.getNetAmount()).append(",")
                    .append(i.getStatus()).append(",")
                    .append(i.getPaidAt() != null ? i.getPaidAt().toString() : "")
                    .append("\n");
        }

        return sb.toString();
    }

    @Override
    public String generateAttendanceCsv(Long societyId, LocalDate date) {
        if (!societyRepository.existsById(societyId)) {
            throw new ResourceNotFoundException("Society not found with id: " + societyId);
        }

        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<Cook> cooks = cookRepository.findBySocietyId(societyId);

        StringBuilder sb = new StringBuilder();
        sb.append("CookName,BookingId,FlatNumber,TowerName,AttendanceDate,CheckInTime,CheckOutTime,Status,Remarks\n");

        for (Cook cook : cooks) {
            List<Attendance> attendances = attendanceRepository.findByCookIdAndAttendanceDate(cook.getId(), targetDate);
            for (Attendance a : attendances) {
                sb.append("\"").append(cook.getUser().getFullName()).append("\",")
                        .append(a.getBooking().getId()).append(",")
                        .append(a.getBooking().getApartment().getFlatNumber()).append(",")
                        .append(a.getBooking().getSlot().getTower().getName()).append(",")
                        .append(a.getAttendanceDate()).append(",")
                        .append(a.getCheckInTime() != null ? a.getCheckInTime().toString() : "").append(",")
                        .append(a.getCheckOutTime() != null ? a.getCheckOutTime().toString() : "").append(",")
                        .append(a.getStatus()).append(",")
                        .append("\"").append(a.getRemarks() != null ? a.getRemarks() : "").append("\"\n");
            }
        }

        return sb.toString();
    }
}

