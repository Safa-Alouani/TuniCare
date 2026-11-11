package com.tunicare.coverage_service.controller;

import com.tunicare.coverage_service.entity.Reimbursement;
import com.tunicare.coverage_service.service.ReimbursementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/reimbursements")
public class ReimbursementController {

    @Autowired
    private ReimbursementService reimbursementService;

    // On crée une petite classe pour recevoir les données de Postman
    public static class ReimbursementRequest {
        public Long patientId;
        public BigDecimal totalAmount;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reimbursement createReimbursement(@RequestBody ReimbursementRequest request) {
        return reimbursementService.calculateReimbursement(request.patientId, request.totalAmount);
    }
}