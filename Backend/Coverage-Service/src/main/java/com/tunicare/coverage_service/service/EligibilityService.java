package com.tunicare.coverage_service.service;

import com.tunicare.coverage_service.entity.BeneficiaryCoverage;
import com.tunicare.coverage_service.entity.EligibilityCheck;
import com.tunicare.coverage_service.repository.BeneficiaryCoverageRepository;
import com.tunicare.coverage_service.repository.EligibilityCheckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tunicare.coverage_service.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EligibilityService {

    @Autowired
    private BeneficiaryCoverageRepository beneficiaryCoverageRepository;

    @Autowired
    private EligibilityCheckRepository eligibilityCheckRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    public String checkEligibility(Long patientId) {
        Optional<BeneficiaryCoverage> coverageOpt = beneficiaryCoverageRepository.findByPatientId(patientId);

        // On prépare l'objet pour l'historique
        EligibilityCheck checkRecord = new EligibilityCheck();
        checkRecord.setPatientId(patientId);
        checkRecord.setCheckDate(LocalDateTime.now());

        // Si le patient n'a pas de couverture
        if (coverageOpt.isEmpty()) {
            checkRecord.setEligible(false);
            checkRecord.setReason("Aucune couverture trouvée");
            eligibilityCheckRepository.save(checkRecord);
            return "NON ELIGIBLE : Aucune couverture trouvée pour ce patient.";
        }

        BeneficiaryCoverage coverage = coverageOpt.get();
        LocalDate today = LocalDate.now();
        checkRecord.setBeneficiaryCoverage(coverage);

        // Si la couverture n'est pas active
        if (!"ACTIVE".equals(coverage.getStatus())) {
            checkRecord.setEligible(false);
            checkRecord.setReason("Couverture inactive");
            eligibilityCheckRepository.save(checkRecord);
            return "NON ELIGIBLE : La couverture est inactive (Statut : " + coverage.getStatus() + ").";
        }
        
        // Si la date n'est pas valide
        if (today.isBefore(coverage.getStartDate()) || today.isAfter(coverage.getEndDate())) {
            checkRecord.setEligible(false);
            checkRecord.setReason("Couverture expirée ou pas encore valide");
            eligibilityCheckRepository.save(checkRecord);
            return "NON ELIGIBLE : La couverture est expirée ou pas encore valide.";
        }

        // Si tout est bon
        checkRecord.setEligible(true);
        checkRecord.setReason("Patient éligible");
        eligibilityCheckRepository.save(checkRecord);
         // On prévient les autres services que l'éligibilité a été vérifiée
        String message = "EligibilityChecked pour le patient : " + patientId;
        rabbitTemplate.convertAndSend(RabbitMQConfig.ELIGIBILITY_QUEUE, message);
        return "ELIGIBLE : Patient couvert. Taux de base 80%.";
    }
}