package com.cookcircle.repository;

import com.cookcircle.entity.BackupAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BackupAssignmentRepository extends JpaRepository<BackupAssignment, Long> {
    List<BackupAssignment> findByLeaveRequestId(Long leaveRequestId);
    List<BackupAssignment> findByBackupCookId(Long backupCookId);
    List<BackupAssignment> findByPrimaryCookId(Long primaryCookId);
    List<BackupAssignment> findByTowerIdAndAssignmentDate(Long towerId, LocalDate assignmentDate);
    List<BackupAssignment> findByBackupCookIdAndAssignmentDate(Long backupCookId, LocalDate assignmentDate);
}

