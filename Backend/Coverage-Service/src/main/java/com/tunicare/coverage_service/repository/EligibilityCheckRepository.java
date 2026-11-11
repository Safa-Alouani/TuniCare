package com.tunicare.coverage_service.repository;

import com.tunicare.coverage_service.entity.EligibilityCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EligibilityCheckRepository extends JpaRepository<EligibilityCheck, Long> {
}