package com.tunicare.coverage_service.service;

import com.tunicare.coverage_service.entity.BeneficiaryCoverage;
import com.tunicare.coverage_service.entity.Reimbursement;
import com.tunicare.coverage_service.repository.BeneficiaryCoverageRepository;
import com.tunicare.coverage_service.repository.ReimbursementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ReimbursementService {

    @Autowired
    private BeneficiaryCoverageRepository beneficiaryCoverageRepository;

    @Autowired
    private ReimbursementRepository reimbursementRepository;

    public Reimbursement calculateReimbursement(Long patientId, BigDecimal totalAmount) {
        // 1. On cherche la couverture du patient
        Optional<BeneficiaryCoverage> coverageOpt = beneficiaryCoverageRepository.findByPatientId(patientId);

        if (coverageOpt.isEmpty()) {
            throw new RuntimeException("Patient non trouvé ou non assuré.");
        }

        BeneficiaryCoverage coverage = coverageOpt.get();

        // 2. On crée le remboursement
        Reimbursement reimbursement = new Reimbursement();
        reimbursement.setTotalAmount(totalAmount);
        reimbursement.setRequestDate(LocalDateTime.now());
        reimbursement.setBeneficiaryCoverage(coverage);
        reimbursement.setStatus("CALCULATED");

        // 3. Le calcul : On applique un taux de 80% (0.80)
        BigDecimal tauxRemboursement = new BigDecimal("0.80");
        BigDecimal montantRembourse = totalAmount.multiply(tauxRemboursement);
        
        reimbursement.setReimbursedAmount(montantRembourse);

        // 4. On sauvegarde dans la base
        return reimbursementRepository.save(reimbursement);
    }
}