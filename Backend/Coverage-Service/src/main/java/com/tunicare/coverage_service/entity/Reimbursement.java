package com.tunicare.coverage_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Reimbursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal totalAmount;      // Le montant total de la facture (ex: 100 DT)
    private BigDecimal reimbursedAmount; // Ce que l'assurance rembourse (ex: 80 DT)
    private LocalDateTime requestDate;
    private String status;               // Ex: "CALCULATED"

    // On lie ce remboursement à la couverture du patient
    @ManyToOne
    @JoinColumn(name = "beneficiary_coverage_id")
    private BeneficiaryCoverage beneficiaryCoverage;
}