package com.cookcircle.service;

import com.cookcircle.dto.complaint.ComplaintDto;
import com.cookcircle.dto.complaint.CreateComplaintRequest;
import com.cookcircle.dto.complaint.ResolveComplaintRequest;

import java.util.List;

public interface ComplaintService {
    ComplaintDto createComplaint(Long residentUserId, CreateComplaintRequest request);
    List<ComplaintDto> getResidentComplaints(Long residentUserId);
    List<ComplaintDto> getSocietyComplaints(Long societyId, String status);
    ComplaintDto resolveComplaint(Long adminUserId, Long complaintId, ResolveComplaintRequest request);
}

