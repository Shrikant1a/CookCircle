package com.cookcircle.service;

import com.cookcircle.dto.society.CreateSocietyRequest;
import com.cookcircle.dto.society.SocietyDto;

import java.util.List;

public interface SocietyService {
    List<SocietyDto> getAllSocieties();
    SocietyDto getSocietyById(Long id);
    SocietyDto getSocietyByCode(String code);
    SocietyDto createSociety(CreateSocietyRequest request);
    SocietyDto updateSociety(Long id, CreateSocietyRequest request);
    void deleteSociety(Long id);
}

