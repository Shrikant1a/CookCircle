package com.cookcircle.repository;

import com.cookcircle.entity.Society;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocietyRepository extends JpaRepository<Society, Long> {
    Optional<Society> findByCode(String code);
    boolean existsByCode(String code);
}
