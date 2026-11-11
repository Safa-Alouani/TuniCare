package com.tunicare.coverage_service.controller;

import com.tunicare.coverage_service.entity.BeneficiaryCoverage;
import com.tunicare.coverage_service.repository.BeneficiaryCoverageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/beneficiary-coverages")
public class BeneficiaryCoverageController {

    @Autowired
    private BeneficiaryCoverageRepository repository;

    // 1. Créer/Attribuer une couverture (POST)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeneficiaryCoverage assignCoverage(@RequestBody BeneficiaryCoverage newCoverage) {
        return repository.save(newCoverage);
    }

    // 2. Consulter la couverture d'un patient spécifique (GET)
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<BeneficiaryCoverage> getCoverageByPatient(@PathVariable Long patientId) {
        Optional<BeneficiaryCoverage> coverage = repository.findByPatientId(patientId);
        
        if (coverage.isPresent()) {
            return ResponseEntity.ok(coverage.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}