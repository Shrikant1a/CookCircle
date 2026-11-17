package com.cookcircle.repository;

import com.cookcircle.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByResidentId(Long residentId);
    List<Booking> findByResidentIdAndBookingDate(Long residentId, LocalDate bookingDate);
    List<Booking> findByResidentIdAndBookingDateBetweenOrderByBookingDateAsc(Long residentId, LocalDate startDate, LocalDate endDate);
    List<Booking> findByApartmentIdAndBookingDate(Long apartmentId, LocalDate bookingDate);
    List<Booking> findByEffectiveCookIdAndBookingDate(Long cookId, LocalDate bookingDate);
    List<Booking> findByPrimaryCookIdAndBookingDate(Long cookId, LocalDate bookingDate);
    List<Booking> findBySlotTowerIdAndBookingDate(Long towerId, LocalDate bookingDate);
    Optional<Booking> findBySlotIdAndBookingDateAndStatusIn(Long slotId, LocalDate bookingDate, List<String> statuses);
    boolean existsBySlotIdAndBookingDateAndStatusIn(Long slotId, LocalDate bookingDate, List<String> statuses);
    boolean existsByApartmentIdAndBookingDateAndSlotShiftAndStatusIn(Long apartmentId, LocalDate bookingDate, String shift, List<String> statuses);
    boolean existsByEffectiveCookIdAndSlotIdAndBookingDateAndStatusIn(Long cookId, Long slotId, LocalDate bookingDate, List<String> statuses);
}

