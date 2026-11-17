package com.cookcircle.service.impl;

import com.cookcircle.dto.slot.CookingSlotDto;
import com.cookcircle.dto.slot.CreateCookingSlotRequest;
import com.cookcircle.dto.slot.SlotAvailabilityDto;
import com.cookcircle.entity.Booking;
import com.cookcircle.entity.CookAssignment;
import com.cookcircle.entity.CookingSlot;
import com.cookcircle.entity.Tower;
import com.cookcircle.exception.ResourceNotFoundException;
import com.cookcircle.repository.BookingRepository;
import com.cookcircle.repository.CookAssignmentRepository;
import com.cookcircle.repository.CookingSlotRepository;
import com.cookcircle.repository.TowerRepository;
import com.cookcircle.service.CookingSlotService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CookingSlotServiceImpl implements CookingSlotService {

    private final CookingSlotRepository cookingSlotRepository;
    private final TowerRepository towerRepository;
    private final BookingRepository bookingRepository;
    private final CookAssignmentRepository cookAssignmentRepository;

    public CookingSlotServiceImpl(CookingSlotRepository cookingSlotRepository,
                                  TowerRepository towerRepository,
                                  BookingRepository bookingRepository,
                                  CookAssignmentRepository cookAssignmentRepository) {
        this.cookingSlotRepository = cookingSlotRepository;
        this.towerRepository = towerRepository;
        this.bookingRepository = bookingRepository;
        this.cookAssignmentRepository = cookAssignmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CookingSlotDto> getSlotsByTower(Long towerId, String shift) {
        if (!towerRepository.existsById(towerId)) {
            throw new ResourceNotFoundException("Tower not found with id: " + towerId);
        }

        List<CookingSlot> slots;
        if (shift != null && !shift.isBlank()) {
            slots = cookingSlotRepository.findByTowerIdAndShiftOrderBySlotOrderAsc(towerId, shift.trim().toUpperCase());
        } else {
            slots = cookingSlotRepository.findByTowerIdOrderByShiftAscSlotOrderAsc(towerId);
        }

        return slots.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public CookingSlotDto createSlot(Long towerId, CreateCookingSlotRequest request) {
        Tower tower = towerRepository.findById(towerId)
                .orElseThrow(() -> new ResourceNotFoundException("Tower not found with id: " + towerId));

        String shift = request.getShift().trim().toUpperCase();
        if (cookingSlotRepository.existsByTowerIdAndShiftAndSlotOrder(towerId, shift, request.getSlotOrder())) {
            throw new IllegalArgumentException("Slot order " + request.getSlotOrder() + " already exists for shift " + shift + " in this tower");
        }

        CookingSlot slot = new CookingSlot();
        slot.setTower(tower);
        slot.setShift(shift);
        slot.setSlotOrder(request.getSlotOrder());
        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        slot.setIsActive(true);

        CookingSlot saved = cookingSlotRepository.save(slot);
        return mapToDto(saved);
    }

    @Override
    public void initializeDefaultSlotsForTower(Long towerId) {
        Tower tower = towerRepository.findById(towerId)
                .orElseThrow(() -> new ResourceNotFoundException("Tower not found with id: " + towerId));

        LocalTime[][] morningTimes = {
                {LocalTime.of(7, 0), LocalTime.of(7, 45)},
                {LocalTime.of(7, 45), LocalTime.of(8, 30)},
                {LocalTime.of(8, 30), LocalTime.of(9, 15)},
                {LocalTime.of(9, 15), LocalTime.of(10, 0)},
                {LocalTime.of(10, 0), LocalTime.of(10, 45)}
        };

        LocalTime[][] eveningTimes = {
                {LocalTime.of(18, 0), LocalTime.of(18, 45)},
                {LocalTime.of(18, 45), LocalTime.of(19, 30)},
                {LocalTime.of(19, 30), LocalTime.of(20, 15)},
                {LocalTime.of(20, 15), LocalTime.of(21, 0)},
                {LocalTime.of(21, 0), LocalTime.of(21, 45)}
        };

        for (int i = 0; i < morningTimes.length; i++) {
            int slotOrder = i + 1;
            if (!cookingSlotRepository.existsByTowerIdAndShiftAndSlotOrder(towerId, "MORNING", slotOrder)) {
                CookingSlot slot = new CookingSlot();
                slot.setTower(tower);
                slot.setShift("MORNING");
                slot.setSlotOrder(slotOrder);
                slot.setStartTime(morningTimes[i][0]);
                slot.setEndTime(morningTimes[i][1]);
                slot.setIsActive(true);
                cookingSlotRepository.save(slot);
            }
        }

        for (int i = 0; i < eveningTimes.length; i++) {
            int slotOrder = i + 1;
            if (!cookingSlotRepository.existsByTowerIdAndShiftAndSlotOrder(towerId, "EVENING", slotOrder)) {
                CookingSlot slot = new CookingSlot();
                slot.setTower(tower);
                slot.setShift("EVENING");
                slot.setSlotOrder(slotOrder);
                slot.setStartTime(eveningTimes[i][0]);
                slot.setEndTime(eveningTimes[i][1]);
                slot.setIsActive(true);
                cookingSlotRepository.save(slot);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SlotAvailabilityDto> getAvailableSlotsForDate(Long towerId, LocalDate date, String shift) {
        if (!towerRepository.existsById(towerId)) {
            throw new ResourceNotFoundException("Tower not found with id: " + towerId);
        }

        List<CookingSlot> slots;
        if (shift != null && !shift.isBlank()) {
            slots = cookingSlotRepository.findByTowerIdAndShiftOrderBySlotOrderAsc(towerId, shift.trim().toUpperCase());
        } else {
            slots = cookingSlotRepository.findByTowerIdOrderByShiftAscSlotOrderAsc(towerId);
        }

        List<CookAssignment> towerAssignments = cookAssignmentRepository.findByTowerIdAndStatus(towerId, "ACTIVE");

        return slots.stream().map(slot -> {
            SlotAvailabilityDto dto = new SlotAvailabilityDto();
            dto.setSlotId(slot.getId());
            dto.setTowerId(slot.getTower().getId());
            dto.setTowerName(slot.getTower().getName());
            dto.setShift(slot.getShift());
            dto.setSlotOrder(slot.getSlotOrder());
            dto.setStartTime(slot.getStartTime());
            dto.setEndTime(slot.getEndTime());
            dto.setDate(date);

            // Default assigned cook for this shift if available
            towerAssignments.stream()
                    .filter(a -> a.getShift().equalsIgnoreCase("BOTH") || a.getShift().equalsIgnoreCase(slot.getShift()))
                    .findFirst()
                    .ifPresent(a -> {
                        String name = a.getCook().getUser().getFullName();
                        dto.setPrimaryCookName(name);
                        dto.setEffectiveCookName(name);
                    });

            Optional<Booking> activeBooking = bookingRepository.findBySlotIdAndBookingDateAndStatusIn(
                    slot.getId(), date, Arrays.asList("CONFIRMED", "COOKING", "COMPLETED")
            );

            if (activeBooking.isPresent()) {
                Booking b = activeBooking.get();
                dto.setAvailable(false);
                dto.setBookingId(b.getId());
                dto.setBookedFlatNumber(b.getApartment().getFlatNumber());
                dto.setPrimaryCookName(b.getPrimaryCook().getUser().getFullName());
                dto.setEffectiveCookName(b.getEffectiveCook().getUser().getFullName());
                dto.setBookingStatus(b.getStatus());
            } else {
                dto.setAvailable(true);
                dto.setBookingStatus("AVAILABLE");
            }

            return dto;
        }).collect(Collectors.toList());
    }

    private CookingSlotDto mapToDto(CookingSlot slot) {
        return new CookingSlotDto(
                slot.getId(),
                slot.getTower().getId(),
                slot.getTower().getName(),
                slot.getShift(),
                slot.getSlotOrder(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getIsActive()
        );
    }
}

