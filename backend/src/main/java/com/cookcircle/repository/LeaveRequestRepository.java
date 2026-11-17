package com.cookcircle.repository;

import com.cookcircle.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByCookId(Long cookId);
    List<LeaveRequest> findByCookIdAndStatus(Long cookId, String status);
    List<LeaveRequest> findByCookSocietyId(Long societyId);
    List<LeaveRequest> findByCookSocietyIdAndStatus(Long societyId, String status);
    boolean existsByCookIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long cookId, String status, LocalDate endDate, LocalDate startDate);
}

