package com.cookcircle.repository;

import com.cookcircle.entity.Cook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CookRepository extends JpaRepository<Cook, Long> {
    Optional<Cook> findByUserId(Long userId);
    List<Cook> findBySocietyId(Long societyId);
    List<Cook> findBySocietyIdAndType(Long societyId, String type);
    List<Cook> findBySocietyIdAndStatus(Long societyId, String status);
    List<Cook> findBySocietyIdAndTypeAndStatus(Long societyId, String type, String status);
}
