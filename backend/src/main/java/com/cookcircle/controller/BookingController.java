package com.cookcircle.controller;

import com.cookcircle.dto.ApiResponse;
import com.cookcircle.dto.booking.BookingDto;
import com.cookcircle.dto.booking.CreateBookingRequest;
import com.cookcircle.dto.booking.VerifyOtpRequest;
import com.cookcircle.security.UserPrincipal;
import com.cookcircle.service.BookingService;
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
@RequestMapping("/api/v1/bookings")
@Tag(name = "Bookings", description = "45-Minute Apartment Cooking Slot Bookings and Cook Execution")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @PreAuthorize("hasRole('RESIDENT')")
    @Operation(summary = "Book a 45-minute cooking slot for an apartment")
    public ResponseEntity<ApiResponse<BookingDto>> createBooking(
            @Valid @RequestBody CreateBookingRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        BookingDto booking = bookingService.createBooking(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cooking slot booked successfully", booking));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN', 'RESIDENT', 'COOK')")
    @Operation(summary = "Get booking details by ID")
    public ResponseEntity<ApiResponse<BookingDto>> getBookingById(@PathVariable Long id) {
        BookingDto booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('RESIDENT')")
    @Operation(summary = "Get bookings for the logged-in resident")
    public ResponseEntity<ApiResponse<List<BookingDto>>> getMyBookings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<BookingDto> bookings = bookingService.getResidentBookings(currentUser.getId(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    @GetMapping("/cook-schedule")
    @PreAuthorize("hasRole('COOK')")
    @Operation(summary = "Get daily cooking schedule and apartment route for logged-in cook partner")
    public ResponseEntity<ApiResponse<List<BookingDto>>> getCookSchedule(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<BookingDto> schedule = bookingService.getCookSchedule(currentUser.getId(), date);
        return ResponseEntity.ok(ApiResponse.success(schedule));
    }

    @GetMapping("/tower/{towerId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Get all bookings for a tower on a specific date")
    public ResponseEntity<ApiResponse<List<BookingDto>>> getTowerBookings(
            @PathVariable Long towerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<BookingDto> bookings = bookingService.getTowerBookings(towerId, date);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    @PostMapping("/{id}/start-cooking")
    @PreAuthorize("hasRole('COOK')")
    @Operation(summary = "Cook enters resident's 4-digit arrival OTP to start cooking session")
    public ResponseEntity<ApiResponse<BookingDto>> startCooking(
            @PathVariable Long id,
            @Valid @RequestBody VerifyOtpRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        BookingDto booking = bookingService.startCookingWithOtp(currentUser.getId(), id, request.getOtp());
        return ResponseEntity.ok(ApiResponse.success("OTP verified. Cooking session started!", booking));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('COOK')")
    @Operation(summary = "Cook marks cooking slot completed")
    public ResponseEntity<ApiResponse<BookingDto>> completeBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        BookingDto booking = bookingService.completeBooking(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Cooking session completed successfully", booking));
    }

    @PostMapping("/{id}/skip-meal")
    @PreAuthorize("hasRole('RESIDENT')")
    @Operation(summary = "Resident skips meal slot and receives +₹140 wallet credit")
    public ResponseEntity<ApiResponse<BookingDto>> skipMeal(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        BookingDto booking = bookingService.skipMeal(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Meal skipped successfully. ₹140 credited to your wallet balance.", booking));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('RESIDENT')")
    @Operation(summary = "Resident cancels booking")
    public ResponseEntity<ApiResponse<BookingDto>> cancelBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        BookingDto booking = bookingService.cancelBooking(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully", booking));
    }
}

