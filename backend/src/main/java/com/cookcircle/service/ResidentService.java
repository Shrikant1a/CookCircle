package com.cookcircle.service;

import com.cookcircle.dto.resident.ResidentDto;
import com.cookcircle.dto.resident.UpdateResidentPreferencesRequest;

import java.util.List;

public interface ResidentService {
    List<ResidentDto> getResidentsBySociety(Long societyId);
    ResidentDto getResidentById(Long residentId);
    ResidentDto getResidentByUserId(Long userId);
    ResidentDto updatePreferences(Long residentId, UpdateResidentPreferencesRequest request);
    ResidentDto updateResidentStatus(Long residentId, String status);
}

