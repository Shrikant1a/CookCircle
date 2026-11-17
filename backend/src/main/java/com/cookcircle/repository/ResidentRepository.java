package com.cookcircle.repository;

import com.cookcircle.entity.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResidentRepository extends JpaRepository<Resident, Long> {
    Optional<Resident> findByUserId(Long userId);
    List<Resident> findBySocietyId(Long societyId);
    Optional<Resident> findByApartmentId(Long apartmentId);
    boolean existsByApartmentId(Long apartmentId);
}
