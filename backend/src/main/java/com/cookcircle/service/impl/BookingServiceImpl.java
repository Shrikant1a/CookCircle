package com.cookcircle.service.impl;

import com.cookcircle.dto.booking.BookingDto;
import com.cookcircle.dto.booking.CreateBookingRequest;
import com.cookcircle.entity.*;
import com.cookcircle.exception.ResourceNotFoundException;
import com.cookcircle.exception.SlotConflictException;
import com.cookcircle.repository.*;
import com.cookcircle.service.BookingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final CookingSlotRepository cookingSlotRepository;
    private final ResidentRepository residentRepository;
    private final CookRepository cookRepository;
    private final CookAssignmentRepository cookAssignmentRepository;
    private final TowerRepository towerRepository;
    private final SecureRandom random = new SecureRandom();

    public BookingServiceImpl(BookingRepository bookingRepository,
                              CookingSlotRepository cookingSlotRepository,
                              ResidentRepository residentRepository,
                              CookRepository cookRepository,
                              CookAssignmentRepository cookAssignmentRepository,
                              TowerRepository towerRepository) {
        this.bookingRepository = bookingRepository;
        this.cookingSlotRepository = cookingSlotRepository;
        this.residentRepository = residentRepository;
        this.cookRepository = cookRepository;
        this.cookAssignmentRepository = cookAssignmentRepository;
        this.towerRepository = towerRepository;
    }

    @Override
    public BookingDto createBooking(Long residentUserId, CreateBookingRequest request) {
        Resident resident = residentRepository.findByUserId(residentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Resident profile not found for user: " + residentUserId));

        Apartment apartment = resident.getApartment();
        if (apartment == null) {
            throw new IllegalArgumentException("Resident does not have an assigned apartment");
        }

        CookingSlot slot = cookingSlotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Cooking slot not found with id: " + request.getSlotId()));

        if (!slot.getTower().getId().equals(apartment.getTower().getId())) {
            throw new IllegalArgumentException("Cannot book slots in another residential tower");
        }

        LocalDate bookingDate = request.getBookingDate();
        if (bookingDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot book slots in the past");
        }

        // 1. Conflict Prevention: Slot is already booked for this date
        List<String> blockingStatuses = Arrays.asList("CONFIRMED", "COOKING", "COMPLETED");
        if (bookingRepository.existsBySlotIdAndBookingDateAndStatusIn(slot.getId(), bookingDate, blockingStatuses)) {
            throw new SlotConflictException("This cooking slot is already booked for " + bookingDate);
        }

        // 2. Conflict Prevention: Apartment already has a confirmed booking for this shift
        if (bookingRepository.existsByApartmentIdAndBookingDateAndSlotShiftAndStatusIn(
                apartment.getId(), bookingDate, slot.getShift(), Arrays.asList("CONFIRMED", "COOKING"))) {
            throw new SlotConflictException("Your flat already has an active cooking booking for the " + slot.getShift() + " shift on " + bookingDate);
        }

        // 3. Find primary assigned cook for this tower and shift
        List<CookAssignment> assignments = cookAssignmentRepository.findByTowerIdAndStatus(slot.getTower().getId(), "ACTIVE");
        Cook assignedCook = assignments.stream()
                .filter(a -> a.getShift().equalsIgnoreCase("BOTH") || a.getShift().equalsIgnoreCase(slot.getShift()))
                .map(CookAssignment::getCook)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No active cook is assigned to " + slot.getTower().getName() + " for " + slot.getShift() + " shift"));

        // 4. Generate 4-digit verification OTP
        String otp = String.format("%04d", random.nextInt(10000));

        Booking booking = new Booking();
        booking.setSociety(resident.getSociety());
        booking.setSlot(slot);
        booking.setApartment(apartment);
        booking.setResident(resident);
        booking.setPrimaryCook(assignedCook);
        booking.setEffectiveCook(assignedCook);
        booking.setBookingDate(bookingDate);
        booking.setDishesPlanned(request.getDishesPlanned() != null ? request.getDishesPlanned().trim() : "Custom Menu");
        booking.setArrivalOtp(otp);
        booking.setStatus("CONFIRMED");
        booking.setSkipRefundAmount(BigDecimal.ZERO);

        Booking saved = bookingRepository.save(booking);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        return mapToDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getResidentBookings(Long residentUserId, LocalDate startDate, LocalDate endDate) {
        Resident resident = residentRepository.findByUserId(residentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Resident not found for user: " + residentUserId));

        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(7);
        LocalDate end = endDate != null ? endDate : LocalDate.now().plusDays(30);

        return bookingRepository.findByResidentIdAndBookingDateBetweenOrderByBookingDateAsc(resident.getId(), start, end)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getCookSchedule(Long cookUserId, LocalDate date) {
        Cook cook = cookRepository.findByUserId(cookUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Cook not found for user: " + cookUserId));

        LocalDate targetDate = date != null ? date : LocalDate.now();
        return bookingRepository.findByEffectiveCookIdAndBookingDate(cook.getId(), targetDate)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getTowerBookings(Long towerId, LocalDate date) {
        if (!towerRepository.existsById(towerId)) {
            throw new ResourceNotFoundException("Tower not found with id: " + towerId);
        }

        LocalDate targetDate = date != null ? date : LocalDate.now();
        return bookingRepository.findBySlotTowerIdAndBookingDate(towerId, targetDate)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public BookingDto startCookingWithOtp(Long cookUserId, Long bookingId, String otp) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        Cook cook = cookRepository.findByUserId(cookUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Cook not found for user: " + cookUserId));

        if (!booking.getEffectiveCook().getId().equals(cook.getId())) {
            throw new IllegalArgumentException("Only the assigned cook can start cooking for this booking");
        }

        if (!"CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException("Booking is not in CONFIRMED status (Current status: " + booking.getStatus() + ")");
        }

        if (!booking.getArrivalOtp().equals(otp.trim())) {
            throw new IllegalArgumentException("Invalid arrival OTP entered");
        }

        booking.setStatus("COOKING");
        Booking updated = bookingRepository.save(booking);
        return mapToDto(updated);
    }

    @Override
    public BookingDto completeBooking(Long cookUserId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        Cook cook = cookRepository.findByUserId(cookUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Cook not found for user: " + cookUserId));

        if (!booking.getEffectiveCook().getId().equals(cook.getId())) {
            throw new IllegalArgumentException("Only the assigned cook can complete this booking");
        }

        if (!"COOKING".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException("Booking must be IN_PROGRESS/COOKING before completion");
        }

        booking.setStatus("COMPLETED");
        Booking updated = bookingRepository.save(booking);
        return mapToDto(updated);
    }

    @Override
    public BookingDto skipMeal(Long residentUserId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        Resident resident = residentRepository.findByUserId(residentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Resident not found for user: " + residentUserId));

        if (!booking.getResident().getId().equals(resident.getId())) {
            throw new IllegalArgumentException("Only the booking resident can request to skip a meal");
        }

        if (!"CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException("Cannot skip meal for booking with status: " + booking.getStatus());
        }

        BigDecimal refund = booking.getSociety().getSkipMealCredit() != null
                ? booking.getSociety().getSkipMealCredit()
                : new BigDecimal("140.00");

        resident.setWalletBalance(resident.getWalletBalance().add(refund));
        residentRepository.save(resident);

        booking.setStatus("SKIPPED");
        booking.setSkipRefundAmount(refund);
        Booking updated = bookingRepository.save(booking);
        return mapToDto(updated);
    }

    @Override
    public BookingDto cancelBooking(Long residentUserId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        Resident resident = residentRepository.findByUserId(residentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Resident not found for user: " + residentUserId));

        if (!booking.getResident().getId().equals(resident.getId())) {
            throw new IllegalArgumentException("Only the resident can cancel this booking");
        }

        if (!"CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException("Cannot cancel booking with status: " + booking.getStatus());
        }

        booking.setStatus("CANCELLED");
        Booking updated = bookingRepository.save(booking);
        return mapToDto(updated);
    }

    private BookingDto mapToDto(Booking b) {
        BookingDto dto = new BookingDto();
        dto.setId(b.getId());
        dto.setSocietyId(b.getSociety().getId());
        dto.setSocietyName(b.getSociety().getName());

        dto.setTowerId(b.getSlot().getTower().getId());
        dto.setTowerName(b.getSlot().getTower().getName());

        dto.setSlotId(b.getSlot().getId());
        dto.setSlotOrder(b.getSlot().getSlotOrder());
        dto.setShift(b.getSlot().getShift());
        dto.setStartTime(b.getSlot().getStartTime());
        dto.setEndTime(b.getSlot().getEndTime());

        dto.setApartmentId(b.getApartment().getId());
        dto.setFlatNumber(b.getApartment().getFlatNumber());

        dto.setResidentId(b.getResident().getId());
        dto.setResidentName(b.getResident().getUser().getFullName());
        dto.setResidentPhone(b.getResident().getUser().getPhone());

        dto.setPrimaryCookId(b.getPrimaryCook().getId());
        dto.setPrimaryCookName(b.getPrimaryCook().getUser().getFullName());

        dto.setEffectiveCookId(b.getEffectiveCook().getId());
        dto.setEffectiveCookName(b.getEffectiveCook().getUser().getFullName());

        dto.setBookingDate(b.getBookingDate());
        dto.setDishesPlanned(b.getDishesPlanned());
        dto.setArrivalOtp(b.getArrivalOtp());
        dto.setStatus(b.getStatus());
        dto.setSkipRefundAmount(b.getSkipRefundAmount());
        dto.setCreatedAt(b.getCreatedAt());

        return dto;
    }
}

