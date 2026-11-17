package com.cookcircle.service;

import com.cookcircle.dto.booking.BookingDto;
import com.cookcircle.dto.booking.CreateBookingRequest;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long residentUserId, CreateBookingRequest request);
    BookingDto getBookingById(Long id);
    List<BookingDto> getResidentBookings(Long residentUserId, LocalDate startDate, LocalDate endDate);
    List<BookingDto> getCookSchedule(Long cookUserId, LocalDate date);
    List<BookingDto> getTowerBookings(Long towerId, LocalDate date);
    BookingDto startCookingWithOtp(Long cookUserId, Long bookingId, String otp);
    BookingDto completeBooking(Long cookUserId, Long bookingId);
    BookingDto skipMeal(Long residentUserId, Long bookingId);
    BookingDto cancelBooking(Long residentUserId, Long bookingId);
}

