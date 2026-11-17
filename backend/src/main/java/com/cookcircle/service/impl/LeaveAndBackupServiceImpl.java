package com.cookcircle.service.impl;

import com.cookcircle.dto.leave.ApplyLeaveRequest;
import com.cookcircle.dto.leave.BackupAssignmentDto;
import com.cookcircle.dto.leave.LeaveRequestDto;
import com.cookcircle.dto.leave.ReviewLeaveRequest;
import com.cookcircle.entity.*;
import com.cookcircle.exception.ResourceNotFoundException;
import com.cookcircle.repository.*;
import com.cookcircle.service.LeaveAndBackupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class LeaveAndBackupServiceImpl implements LeaveAndBackupService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final BackupAssignmentRepository backupAssignmentRepository;
    private final CookRepository cookRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final SocietyRepository societyRepository;

    public LeaveAndBackupServiceImpl(LeaveRequestRepository leaveRequestRepository,
                                     BackupAssignmentRepository backupAssignmentRepository,
                                     CookRepository cookRepository,
                                     BookingRepository bookingRepository,
                                     UserRepository userRepository,
                                     SocietyRepository societyRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.backupAssignmentRepository = backupAssignmentRepository;
        this.cookRepository = cookRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.societyRepository = societyRepository;
    }

    @Override
    public LeaveRequestDto applyLeave(Long cookUserId, ApplyLeaveRequest request) {
        Cook cook = cookRepository.findByUserId(cookUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Cook not found for user: " + cookUserId));

        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Leave end date cannot be before start date");
        }

        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot apply for leave in the past");
        }

        // Check overlapping approved leave
        if (leaveRequestRepository.existsByCookIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                cook.getId(), "APPROVED", endDate, startDate)) {
            throw new IllegalArgumentException("An approved leave already overlaps with this date range");
        }

        LeaveRequest leave = new LeaveRequest();
        leave.setCook(cook);
        leave.setStartDate(startDate);
        leave.setEndDate(endDate);
        leave.setReason(request.getReason().trim());
        leave.setStatus("PENDING");

        LeaveRequest saved = leaveRequestRepository.save(leave);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestDto> getCookLeaves(Long cookUserId) {
        Cook cook = cookRepository.findByUserId(cookUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Cook not found for user: " + cookUserId));

        return leaveRequestRepository.findByCookId(cook.getId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestDto> getSocietyLeaves(Long societyId, String status) {
        if (!societyRepository.existsById(societyId)) {
            throw new ResourceNotFoundException("Society not found with id: " + societyId);
        }

        List<LeaveRequest> list;
        if (status != null && !status.isBlank()) {
            list = leaveRequestRepository.findByCookSocietyIdAndStatus(societyId, status.trim().toUpperCase());
        } else {
            list = leaveRequestRepository.findByCookSocietyId(societyId);
        }

        return list.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public LeaveRequestDto approveLeave(Long adminUserId, Long leaveId, ReviewLeaveRequest request) {
        LeaveRequest leave = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + leaveId));

        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found with id: " + adminUserId));

        if (!"PENDING".equalsIgnoreCase(leave.getStatus())) {
            throw new IllegalArgumentException("Only PENDING leave requests can be approved");
        }

        Cook primaryCook = leave.getCook();
        Society society = primaryCook.getSociety();

        // 1. Identify Affected Bookings across all days of the leave
        List<Booking> affectedBookings = new ArrayList<>();
        LocalDate cur = leave.getStartDate();
        while (!cur.isAfter(leave.getEndDate())) {
            List<Booking> dayBookings = bookingRepository.findByPrimaryCookIdAndBookingDate(primaryCook.getId(), cur);
            affectedBookings.addAll(dayBookings.stream()
                    .filter(b -> "CONFIRMED".equalsIgnoreCase(b.getStatus()) || "COOKING".equalsIgnoreCase(b.getStatus()))
                    .toList());
            cur = cur.plusDays(1);
        }

        // 2. Select or auto-match suitable Standby Backup Cook
        Cook backupCook;
        if (request != null && request.getBackupCookId() != null) {
            backupCook = cookRepository.findById(request.getBackupCookId())
                    .orElseThrow(() -> new ResourceNotFoundException("Backup cook not found with id: " + request.getBackupCookId()));
        } else {
            // Auto-recommend: Look for active float cook first
            List<Cook> floatCooks = cookRepository.findBySocietyIdAndTypeAndStatus(society.getId(), "BACKUP_FLOAT_COOK", "ACTIVE");
            if (!floatCooks.isEmpty()) {
                backupCook = floatCooks.get(0);
            } else {
                List<Cook> allActive = cookRepository.findBySocietyIdAndStatus(society.getId(), "ACTIVE");
                backupCook = allActive.stream()
                        .filter(c -> !c.getId().equals(primaryCook.getId()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("No standby backup cook available in this society"));
            }
        }

        // 3. Update Affected Bookings: effectiveCook = backupCook (Zero Meal Disruption!)
        Set<Tower> affectedTowers = new HashSet<>();
        for (Booking b : affectedBookings) {
            b.setEffectiveCook(backupCook);
            bookingRepository.save(b);
            affectedTowers.add(b.getSlot().getTower());
        }

        // 4. Create BackupAssignment records per date & tower
        cur = leave.getStartDate();
        while (!cur.isAfter(leave.getEndDate())) {
            for (Tower tower : affectedTowers) {
                BackupAssignment assignment = new BackupAssignment();
                assignment.setLeaveRequest(leave);
                assignment.setPrimaryCook(primaryCook);
                assignment.setBackupCook(backupCook);
                assignment.setTower(tower);
                assignment.setAssignmentDate(cur);
                assignment.setStatus("ACTIVE");
                assignment.setAssignedBy(admin);
                backupAssignmentRepository.save(assignment);
            }
            cur = cur.plusDays(1);
        }

        // 5. Update primary cook status to ON_LEAVE
        primaryCook.setStatus("ON_LEAVE");
        cookRepository.save(primaryCook);

        // 6. Update Leave Request
        leave.setStatus("APPROVED");
        leave.setActionBy(admin);
        if (request != null && request.getActionNotes() != null) {
            leave.setActionNotes(request.getActionNotes());
        } else {
            leave.setActionNotes("Approved. Backup Cook " + backupCook.getUser().getFullName() + " dispatched with zero meal disruption.");
        }

        LeaveRequest updated = leaveRequestRepository.save(leave);
        LeaveRequestDto dto = mapToDto(updated);
        dto.setAffectedBookingsCount(affectedBookings.size());
        dto.setBackupCookName(backupCook.getUser().getFullName());
        return dto;
    }

    @Override
    public LeaveRequestDto rejectLeave(Long adminUserId, Long leaveId, ReviewLeaveRequest request) {
        LeaveRequest leave = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + leaveId));

        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found with id: " + adminUserId));

        if (!"PENDING".equalsIgnoreCase(leave.getStatus())) {
            throw new IllegalArgumentException("Only PENDING leave requests can be rejected");
        }

        leave.setStatus("REJECTED");
        leave.setActionBy(admin);
        leave.setActionNotes(request != null && request.getActionNotes() != null ? request.getActionNotes() : "Leave request rejected by management");

        LeaveRequest updated = leaveRequestRepository.save(leave);
        return mapToDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BackupAssignmentDto> getBackupAssignments(Long societyId, LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<Cook> backupCooks = cookRepository.findBySocietyIdAndType(societyId, "BACKUP_FLOAT_COOK");

        return backupCooks.stream()
                .flatMap(c -> backupAssignmentRepository.findByBackupCookIdAndAssignmentDate(c.getId(), targetDate).stream())
                .map(this::mapToBackupDto)
                .collect(Collectors.toList());
    }

    private LeaveRequestDto mapToDto(LeaveRequest l) {
        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setId(l.getId());
        dto.setCookId(l.getCook().getId());
        dto.setCookName(l.getCook().getUser().getFullName());
        dto.setSocietyId(l.getCook().getSociety().getId());
        dto.setStartDate(l.getStartDate());
        dto.setEndDate(l.getEndDate());
        dto.setReason(l.getReason());
        dto.setStatus(l.getStatus());
        dto.setCreatedAt(l.getCreatedAt());

        if (l.getActionBy() != null) {
            dto.setActionByName(l.getActionBy().getFullName());
        }
        dto.setActionNotes(l.getActionNotes());

        List<BackupAssignment> backups = backupAssignmentRepository.findByLeaveRequestId(l.getId());
        if (!backups.isEmpty()) {
            dto.setBackupCookName(backups.get(0).getBackupCook().getUser().getFullName());
        }

        return dto;
    }

    private BackupAssignmentDto mapToBackupDto(BackupAssignment b) {
        BackupAssignmentDto dto = new BackupAssignmentDto();
        dto.setId(b.getId());
        dto.setLeaveRequestId(b.getLeaveRequest().getId());
        dto.setPrimaryCookId(b.getPrimaryCook().getId());
        dto.setPrimaryCookName(b.getPrimaryCook().getUser().getFullName());
        dto.setBackupCookId(b.getBackupCook().getId());
        dto.setBackupCookName(b.getBackupCook().getUser().getFullName());
        dto.setTowerId(b.getTower().getId());
        dto.setTowerName(b.getTower().getName());
        dto.setAssignmentDate(b.getAssignmentDate());
        dto.setStatus(b.getStatus());
        dto.setAssignedByName(b.getAssignedBy().getFullName());
        dto.setCreatedAt(b.getCreatedAt());
        return dto;
    }
}

