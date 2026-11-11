package com.tunicare.coverage_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class BeneficiaryCoverage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long patientId; // L'ID du patient (vient d'un autre microservice)
    private LocalDate startDate; // Date de début
    private LocalDate endDate;   // Date de fin
    private String status;       // Ex: "ACTIVE", "EXPIRED"

    // Plusieurs patients peuvent avoir la même couverture (la même formule)
    @ManyToOne
    @JoinColumn(name = "coverage_id")
    private Coverage coverage;
}