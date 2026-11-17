package com.cookcircle.repository;

import com.cookcircle.entity.Tower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TowerRepository extends JpaRepository<Tower, Long> {
    List<Tower> findBySocietyId(Long societyId);
    Optional<Tower> findBySocietyIdAndName(Long societyId, String name);
    boolean existsBySocietyIdAndName(Long societyId, String name);
}
