package com.cookcircle.service;

import com.cookcircle.dto.attendance.AttendanceDto;
import com.cookcircle.dto.attendance.LogAttendanceRequest;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    AttendanceDto logAttendance(Long cookUserId, LogAttendanceRequest request);
    List<AttendanceDto> getCookAttendance(Long cookUserId, LocalDate startDate, LocalDate endDate);
    List<AttendanceDto> getDailySocietyAttendance(Long societyId, LocalDate date);
}

