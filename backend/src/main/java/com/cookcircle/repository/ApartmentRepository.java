package com.cookcircle.repository;

import com.cookcircle.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
    List<Apartment> findByTowerId(Long towerId);
    Optional<Apartment> findByTowerIdAndFlatNumber(Long towerId, String flatNumber);
    boolean existsByTowerIdAndFlatNumber(Long towerId, String flatNumber);
    List<Apartment> findByTowerSocietyId(Long societyId);
}
