package com.cookcircle.repository;

import com.cookcircle.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByResidentIdOrderByCreatedAtDesc(Long residentId);
    List<Complaint> findByResidentSocietyIdOrderByCreatedAtDesc(Long societyId);
    List<Complaint> findByResidentSocietyIdAndStatusOrderByCreatedAtDesc(Long societyId, String status);
    long countByResidentSocietyIdAndStatus(Long societyId, String status);
}

