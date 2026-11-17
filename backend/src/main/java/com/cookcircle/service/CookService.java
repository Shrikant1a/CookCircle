package com.cookcircle.service;

import com.cookcircle.dto.cook.AssignCookRequest;
import com.cookcircle.dto.cook.CookAssignmentDto;
import com.cookcircle.dto.cook.CookDto;
import com.cookcircle.dto.cook.CreateCookRequest;
import com.cookcircle.dto.cook.UpdateCookProfileRequest;

import java.util.List;

public interface CookService {
    List<CookDto> getCooksBySociety(Long societyId, String status, String type);
    CookDto getCookById(Long id);
    CookDto getCookByUserId(Long userId);
    CookDto createCook(Long societyId, CreateCookRequest request);
    CookDto updateCookProfile(Long id, UpdateCookProfileRequest request);
    CookDto updateCookVerification(Long id, Boolean policeVerified, Boolean aadhaarVerified, String vaccinationStatus);
    CookDto updateCookStatus(Long id, String status);

    CookAssignmentDto assignCookToTower(Long cookId, AssignCookRequest request);
    List<CookAssignmentDto> getCookAssignments(Long cookId);
    List<CookAssignmentDto> getTowerCookAssignments(Long towerId);
    void cancelCookAssignment(Long assignmentId);
}

