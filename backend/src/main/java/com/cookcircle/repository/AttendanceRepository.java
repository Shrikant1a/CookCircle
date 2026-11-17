package com.cookcircle.repository;

import com.cookcircle.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByCookId(Long cookId);
    List<Attendance> findByCookIdAndAttendanceDate(Long cookId, LocalDate attendanceDate);
    List<Attendance> findByCookIdAndAttendanceDateBetween(Long cookId, LocalDate startDate, LocalDate endDate);
    List<Attendance> findByBookingId(Long bookingId);
    Optional<Attendance> findByBookingIdAndCookId(Long bookingId, Long cookId);
}

