package com.cookcircle.repository;

import com.cookcircle.entity.CookAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CookAssignmentRepository extends JpaRepository<CookAssignment, Long> {
    List<CookAssignment> findByCookId(Long cookId);
    List<CookAssignment> findByCookIdAndStatus(Long cookId, String status);
    List<CookAssignment> findByTowerId(Long towerId);
    List<CookAssignment> findByTowerIdAndStatus(Long towerId, String status);
    List<CookAssignment> findByCookIdAndShiftAndStatus(Long cookId, String shift, String status);
    boolean existsByCookIdAndTowerIdAndShiftAndStatus(Long cookId, Long towerId, String shift, String status);
}

