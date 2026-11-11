package com.tunicare.coverage_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class EligibilityCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long patientId;
    private LocalDateTime checkDate;
    private Boolean eligible;
    private String reason; // Pourquoi c'est éligible ou non

    @ManyToOne
    @JoinColumn(name = "beneficiary_coverage_id")
    private BeneficiaryCoverage beneficiaryCoverage;
}