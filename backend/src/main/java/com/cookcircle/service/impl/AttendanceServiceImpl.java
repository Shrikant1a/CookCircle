package com.cookcircle.service.impl;

import com.cookcircle.dto.attendance.AttendanceDto;
import com.cookcircle.dto.attendance.LogAttendanceRequest;
import com.cookcircle.entity.Attendance;
import com.cookcircle.entity.Booking;
import com.cookcircle.entity.Cook;
import com.cookcircle.exception.ResourceNotFoundException;
import com.cookcircle.repository.AttendanceRepository;
import com.cookcircle.repository.BookingRepository;
import com.cookcircle.repository.CookRepository;
import com.cookcircle.service.AttendanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final BookingRepository bookingRepository;
    private final CookRepository cookRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository,
                                 BookingRepository bookingRepository,
                                 CookRepository cookRepository) {
        this.attendanceRepository = attendanceRepository;
        this.bookingRepository = bookingRepository;
        this.cookRepository = cookRepository;
    }

    @Override
    public AttendanceDto logAttendance(Long cookUserId, LogAttendanceRequest request) {
        Cook cook = cookRepository.findByUserId(cookUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Cook not found for user: " + cookUserId));

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + request.getBookingId()));

        Optional<Attendance> existing = attendanceRepository.findByBookingIdAndCookId(booking.getId(), cook.getId());
        Attendance attendance;
        if (existing.isPresent()) {
            attendance = existing.get();
            attendance.setCheckOutTime(LocalDateTime.now());
            if (request.getStatus() != null) {
                attendance.setStatus(request.getStatus());
            }
            if (request.getRemarks() != null) {
                attendance.setRemarks(request.getRemarks());
            }
        } else {
            attendance = new Attendance();
            attendance.setCook(cook);
            attendance.setBooking(booking);
            attendance.setAttendanceDate(booking.getBookingDate());
            attendance.setCheckInTime(LocalDateTime.now());
            attendance.setStatus(request.getStatus() != null ? request.getStatus().trim().toUpperCase() : "ON_TIME");
            attendance.setRemarks(request.getRemarks());
        }

        Attendance saved = attendanceRepository.save(attendance);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDto> getCookAttendance(Long cookUserId, LocalDate startDate, LocalDate endDate) {
        Cook cook = cookRepository.findByUserId(cookUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Cook not found for user: " + cookUserId));

        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(30);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        return attendanceRepository.findByCookIdAndAttendanceDateBetween(cook.getId(), start, end)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDto> getDailySocietyAttendance(Long societyId, LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<Cook> cooks = cookRepository.findBySocietyId(societyId);

        return cooks.stream()
                .flatMap(c -> attendanceRepository.findByCookIdAndAttendanceDate(c.getId(), targetDate).stream())
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private AttendanceDto mapToDto(Attendance a) {
        AttendanceDto dto = new AttendanceDto();
        dto.setId(a.getId());
        dto.setCookId(a.getCook().getId());
        dto.setCookName(a.getCook().getUser().getFullName());
        dto.setBookingId(a.getBooking().getId());
        dto.setFlatNumber(a.getBooking().getApartment().getFlatNumber());
        dto.setTowerName(a.getBooking().getSlot().getTower().getName());
        dto.setAttendanceDate(a.getAttendanceDate());
        dto.setCheckInTime(a.getCheckInTime());
        dto.setCheckOutTime(a.getCheckOutTime());
        dto.setStatus(a.getStatus());
        dto.setRemarks(a.getRemarks());
        return dto;
    }
}

