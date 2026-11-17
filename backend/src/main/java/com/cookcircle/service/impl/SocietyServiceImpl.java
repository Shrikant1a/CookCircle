package com.cookcircle.service.impl;

import com.cookcircle.dto.society.CreateSocietyRequest;
import com.cookcircle.dto.society.SocietyDto;
import com.cookcircle.dto.society.TowerDto;
import com.cookcircle.entity.Society;
import com.cookcircle.entity.Tower;
import com.cookcircle.exception.ResourceNotFoundException;
import com.cookcircle.repository.SocietyRepository;
import com.cookcircle.repository.TowerRepository;
import com.cookcircle.service.SocietyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SocietyServiceImpl implements SocietyService {

    private final SocietyRepository societyRepository;
    private final TowerRepository towerRepository;

    public SocietyServiceImpl(SocietyRepository societyRepository, TowerRepository towerRepository) {
        this.societyRepository = societyRepository;
        this.towerRepository = towerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SocietyDto> getAllSocieties() {
        return societyRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SocietyDto getSocietyById(Long id) {
        Society society = societyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Society not found with id: " + id));
        return mapToDto(society);
    }

    @Override
    @Transactional(readOnly = true)
    public SocietyDto getSocietyByCode(String code) {
        Society society = societyRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Society not found with code: " + code));
        return mapToDto(society);
    }

    @Override
    public SocietyDto createSociety(CreateSocietyRequest request) {
        if (societyRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Society with code already exists: " + request.getCode());
        }

        Society society = new Society();
        society.setCode(request.getCode().toUpperCase().trim());
        society.setName(request.getName().trim());
        society.setAddressLine(request.getAddressLine().trim());
        society.setCity(request.getCity().trim());
        society.setState(request.getState().trim());
        society.setPostalCode(request.getPostalCode().trim());
        if (request.getBasePoolingFee() != null) {
            society.setBasePoolingFee(request.getBasePoolingFee());
        }
        if (request.getSkipMealCredit() != null) {
            society.setSkipMealCredit(request.getSkipMealCredit());
        }
        society.setStatus("ACTIVE");

        Society saved = societyRepository.save(society);
        return mapToDto(saved);
    }

    @Override
    public SocietyDto updateSociety(Long id, CreateSocietyRequest request) {
        Society society = societyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Society not found with id: " + id));

        society.setName(request.getName().trim());
        society.setAddressLine(request.getAddressLine().trim());
        society.setCity(request.getCity().trim());
        society.setState(request.getState().trim());
        society.setPostalCode(request.getPostalCode().trim());
        if (request.getBasePoolingFee() != null) {
            society.setBasePoolingFee(request.getBasePoolingFee());
        }
        if (request.getSkipMealCredit() != null) {
            society.setSkipMealCredit(request.getSkipMealCredit());
        }

        Society updated = societyRepository.save(society);
        return mapToDto(updated);
    }

    @Override
    public void deleteSociety(Long id) {
        if (!societyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Society not found with id: " + id);
        }
        societyRepository.deleteById(id);
    }

    private SocietyDto mapToDto(Society society) {
        SocietyDto dto = new SocietyDto();
        dto.setId(society.getId());
        dto.setCode(society.getCode());
        dto.setName(society.getName());
        dto.setAddressLine(society.getAddressLine());
        dto.setCity(society.getCity());
        dto.setState(society.getState());
        dto.setPostalCode(society.getPostalCode());
        dto.setBasePoolingFee(society.getBasePoolingFee());
        dto.setSkipMealCredit(society.getSkipMealCredit());
        dto.setStatus(society.getStatus());

        List<Tower> towers = towerRepository.findBySocietyId(society.getId());
        List<TowerDto> towerDtos = towers.stream().map(t -> {
            TowerDto td = new TowerDto();
            td.setId(t.getId());
            td.setSocietyId(society.getId());
            td.setSocietyName(society.getName());
            td.setName(t.getName());
            td.setTotalFloors(t.getTotalFloors());
            return td;
        }).collect(Collectors.toList());

        dto.setTowers(towerDtos);
        return dto;
    }
}

