package com.tunicare.coverage_service.repository;

import com.tunicare.coverage_service.entity.BeneficiaryCoverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BeneficiaryCoverageRepository extends JpaRepository<BeneficiaryCoverage, Long> {
    // Spring Boot va générer tout seul la requête SQL pour trouver la couverture d'un patient !
    Optional<BeneficiaryCoverage> findByPatientId(Long patientId);
}