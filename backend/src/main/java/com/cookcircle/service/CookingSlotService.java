package com.cookcircle.service;

import com.cookcircle.dto.slot.CookingSlotDto;
import com.cookcircle.dto.slot.CreateCookingSlotRequest;
import com.cookcircle.dto.slot.SlotAvailabilityDto;

import java.time.LocalDate;
import java.util.List;

public interface CookingSlotService {
    List<CookingSlotDto> getSlotsByTower(Long towerId, String shift);
    CookingSlotDto createSlot(Long towerId, CreateCookingSlotRequest request);
    void initializeDefaultSlotsForTower(Long towerId);
    List<SlotAvailabilityDto> getAvailableSlotsForDate(Long towerId, LocalDate date, String shift);
}

