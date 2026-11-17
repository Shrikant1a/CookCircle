package com.cookcircle.service.impl;

import com.cookcircle.dto.complaint.ComplaintDto;
import com.cookcircle.dto.complaint.CreateComplaintRequest;
import com.cookcircle.dto.complaint.ResolveComplaintRequest;
import com.cookcircle.entity.Complaint;
import com.cookcircle.entity.Resident;
import com.cookcircle.exception.ResourceNotFoundException;
import com.cookcircle.repository.ComplaintRepository;
import com.cookcircle.repository.ResidentRepository;
import com.cookcircle.repository.SocietyRepository;
import com.cookcircle.service.ComplaintService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ResidentRepository residentRepository;
    private final SocietyRepository societyRepository;

    public ComplaintServiceImpl(ComplaintRepository complaintRepository,
                                ResidentRepository residentRepository,
                                SocietyRepository societyRepository) {
        this.complaintRepository = complaintRepository;
        this.residentRepository = residentRepository;
        this.societyRepository = societyRepository;
    }

    @Override
    public ComplaintDto createComplaint(Long residentUserId, CreateComplaintRequest request) {
        Resident resident = residentRepository.findByUserId(residentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Resident not found for user: " + residentUserId));

        Complaint complaint = new Complaint();
        complaint.setResident(resident);
        complaint.setCategory(request.getCategory().trim());
        complaint.setSubject(request.getSubject().trim());
        complaint.setDescription(request.getDescription().trim());
        complaint.setStatus("OPEN");

        Complaint saved = complaintRepository.save(complaint);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintDto> getResidentComplaints(Long residentUserId) {
        Resident resident = residentRepository.findByUserId(residentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Resident not found for user: " + residentUserId));

        return complaintRepository.findByResidentIdOrderByCreatedAtDesc(resident.getId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintDto> getSocietyComplaints(Long societyId, String status) {
        if (!societyRepository.existsById(societyId)) {
            throw new ResourceNotFoundException("Society not found with id: " + societyId);
        }

        List<Complaint> list;
        if (status != null && !status.isBlank()) {
            list = complaintRepository.findByResidentSocietyIdAndStatusOrderByCreatedAtDesc(societyId, status.trim().toUpperCase());
        } else {
            list = complaintRepository.findByResidentSocietyIdOrderByCreatedAtDesc(societyId);
        }

        return list.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public ComplaintDto resolveComplaint(Long adminUserId, Long complaintId, ResolveComplaintRequest request) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + complaintId));

        complaint.setStatus("RESOLVED");
        complaint.setResolutionNotes(request.getResolutionNotes().trim());

        Complaint saved = complaintRepository.save(complaint);
        return mapToDto(saved);
    }

    private ComplaintDto mapToDto(Complaint c) {
        ComplaintDto dto = new ComplaintDto();
        dto.setId(c.getId());
        dto.setResidentId(c.getResident().getId());
        dto.setResidentName(c.getResident().getUser().getFullName());
        dto.setFlatNumber(c.getResident().getApartment().getFlatNumber());
        dto.setTowerName(c.getResident().getApartment().getTower().getName());
        dto.setCategory(c.getCategory());
        dto.setSubject(c.getSubject());
        dto.setDescription(c.getDescription());
        dto.setStatus(c.getStatus());
        dto.setResolutionNotes(c.getResolutionNotes());
        dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }
}

