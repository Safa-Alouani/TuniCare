package com.tunicare.coverage_service.controller;

import com.tunicare.coverage_service.service.EligibilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eligibility")
public class EligibilityController {

    @Autowired
    private EligibilityService eligibilityService;

    @GetMapping("/{patientId}")
    public String checkEligibility(@PathVariable Long patientId) {
        return eligibilityService.checkEligibility(patientId);
    }
}