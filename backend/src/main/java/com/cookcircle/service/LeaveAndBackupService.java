package com.cookcircle.service;

import com.cookcircle.dto.leave.ApplyLeaveRequest;
import com.cookcircle.dto.leave.BackupAssignmentDto;
import com.cookcircle.dto.leave.LeaveRequestDto;
import com.cookcircle.dto.leave.ReviewLeaveRequest;

import java.time.LocalDate;
import java.util.List;

public interface LeaveAndBackupService {
    LeaveRequestDto applyLeave(Long cookUserId, ApplyLeaveRequest request);
    List<LeaveRequestDto> getCookLeaves(Long cookUserId);
    List<LeaveRequestDto> getSocietyLeaves(Long societyId, String status);
    LeaveRequestDto approveLeave(Long adminUserId, Long leaveId, ReviewLeaveRequest request);
    LeaveRequestDto rejectLeave(Long adminUserId, Long leaveId, ReviewLeaveRequest request);
    List<BackupAssignmentDto> getBackupAssignments(Long societyId, LocalDate date);
}

