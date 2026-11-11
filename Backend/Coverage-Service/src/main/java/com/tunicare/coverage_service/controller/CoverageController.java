package com.tunicare.coverage_service.controller;

import com.tunicare.coverage_service.entity.Coverage;
import com.tunicare.coverage_service.repository.CoverageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/api/coverages")
public class CoverageController {

    @Autowired
    private CoverageRepository coverageRepository;

    @GetMapping
    public List<Coverage> getAllCoverages() {
        return coverageRepository.findAll();
    }
        // Cette méthode répond quand quelqu'un envoie des données (POST) vers /api/coverages
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Coverage createCoverage(@RequestBody Coverage newCoverage) {
        // On demande au bibliothécaire de sauvegarder la nouvelle couverture
        return coverageRepository.save(newCoverage);
    }






}