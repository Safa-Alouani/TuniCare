package com.tunicare.coverage_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
public class CoverageRight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceType; // Exemple : "Consultation", "Radiologie"
    private BigDecimal reimbursementRate; // Exemple : 80.00 (pour 80%)
    private BigDecimal limitAmount; // Le plafond spécifique à ce droit

    // Plusieurs droits appartiennent à une seule couverture
    @ManyToOne
    @JoinColumn(name = "coverage_id")
    private Coverage coverage;
}