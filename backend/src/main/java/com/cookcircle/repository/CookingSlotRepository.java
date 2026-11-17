package com.cookcircle.repository;

import com.cookcircle.entity.CookingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CookingSlotRepository extends JpaRepository<CookingSlot, Long> {
    List<CookingSlot> findByTowerId(Long towerId);
    List<CookingSlot> findByTowerIdAndShift(Long towerId, String shift);
    List<CookingSlot> findByTowerIdAndShiftOrderBySlotOrderAsc(Long towerId, String shift);
    List<CookingSlot> findByTowerIdOrderByShiftAscSlotOrderAsc(Long towerId);
    Optional<CookingSlot> findByTowerIdAndShiftAndSlotOrder(Long towerId, String shift, Integer slotOrder);
    boolean existsByTowerIdAndShiftAndSlotOrder(Long towerId, String shift, Integer slotOrder);
}

