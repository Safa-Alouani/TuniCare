package com.tunicare.evaluation_service.service;

import com.tunicare.evaluation_service.client.AppointmentClient;
import com.tunicare.evaluation_service.dto.EvaluationRequest;
import com.tunicare.evaluation_service.dto.EvaluationResponse;
import com.tunicare.evaluation_service.messaging.EvaluationEventPublisher;
import com.tunicare.evaluation_service.messaging.event.EvaluationCreatedEvent;
import com.tunicare.evaluation_service.model.Evaluation;
import com.tunicare.evaluation_service.repository.EvaluationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class EvaluationService {

    private static final Logger logger = LoggerFactory.getLogger(EvaluationService.class);

    private final EvaluationRepository evaluationRepository;
    private final AppointmentClient appointmentClient;
    private final EvaluationEventPublisher evaluationEventPublisher;

    // Registre / Cache des eligibilites recues via RabbitMQ
    private final Map<String, String> eligibleAppointments = new ConcurrentHashMap<>();

    @Autowired
    public EvaluationService(EvaluationRepository evaluationRepository,
                             AppointmentClient appointmentClient,
                             EvaluationEventPublisher evaluationEventPublisher) {
        this.evaluationRepository = evaluationRepository;
        this.appointmentClient = appointmentClient;
        this.evaluationEventPublisher = evaluationEventPublisher;
    }

    /**
     * Enregistre l'eligibilite d'un patient recue depuis RabbitMQ (AppointmentCompletedEvent)
     */
    public void registerEligibility(String appointmentId, String patientId, String providerId) {
        eligibleAppointments.put(appointmentId, patientId != null ? patientId : "");
        logger.info("Eligibilite enregistree en memoire : appointmentId={}, patientId={}, providerId={}",
                appointmentId, patientId, providerId);
    }

    /**
     * Etape 1 a 7 : Ajouter une evaluation
     */
    public EvaluationResponse createEvaluation(EvaluationRequest request) {
        String appointmentId = request.getAppointmentId();
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'identifiant du rendez-vous est obligatoire.");
        }

        // 1. Verifier le rendez-vous avec Appointment-Service (Fatma)
        AppointmentClient.AppointmentDto appointment = null;
        try {
            Optional<AppointmentClient.AppointmentDto> aptOpt = appointmentClient.getAppointmentById(appointmentId);
            if (aptOpt.isPresent()) {
                appointment = aptOpt.get();
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Rendez-vous introuvable auprès de Appointment-Service : " + appointmentId);
            }
        } catch (IllegalStateException e) {
            // Si Appointment-Service est indisponible en developpement local, verifier l'eligibilite RabbitMQ
            logger.warn("Appointment-Service non joignable ({}), verification de l'eligibilite RabbitMQ.", e.getMessage());
            if (!eligibleAppointments.containsKey(appointmentId)) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                        "Appointment-Service inaccessible et aucune eligibilite RabbitMQ prealable trouvee pour : " + appointmentId);
            }
        }

        // 2. Verifier que le rendez-vous est termine (COMPLETED)
        if (appointment != null) {
            if (!"COMPLETED".equalsIgnoreCase(appointment.getStatus())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Impossible d'évaluer un rendez-vous non terminé. Statut actuel : " + appointment.getStatus());
            }
        }

        // Determiner et verifier patientId et providerId
        String patientId = request.getPatientId();
        String providerId = request.getProviderId();

        if (appointment != null) {
            // 3. Verifier que le patient est autorise a evaluer
            if (patientId != null && !patientId.trim().isEmpty() && !patientId.equals(appointment.getPatientId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Ce patient n'est pas autorisé à évaluer ce rendez-vous.");
            }
            if (patientId == null || patientId.trim().isEmpty()) {
                patientId = appointment.getPatientId();
            }
            if (providerId == null || providerId.trim().isEmpty()) {
                providerId = appointment.getProviderId();
            }
        } else {
            String eligiblePatient = eligibleAppointments.get(appointmentId);
            if (patientId != null && !eligiblePatient.isEmpty() && !patientId.equals(eligiblePatient)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Le patient n'est pas autorise pour ce rendez-vous.");
            }
            if (patientId == null && !eligiblePatient.isEmpty()) {
                patientId = eligiblePatient;
            }
        }

        // 4. Verifier qu'une evaluation n'existe pas deja pour ce rendez-vous
        Optional<Evaluation> existingEvaluation = evaluationRepository.findByAppointmentId(appointmentId);
        if (existingEvaluation.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Une évaluation existe déjà pour ce rendez-vous (ID: " + appointmentId + ").");
        }

        // 5. Enregistrer l'evaluation dans Firestore
        Date now = new Date();
        Evaluation evaluation = new Evaluation();
        evaluation.setAppointmentId(appointmentId);
        evaluation.setPatientId(patientId);
        evaluation.setProviderId(providerId);
        evaluation.setRating(request.getRating());
        evaluation.setComment(request.getComment());
        evaluation.setCreatedAt(now);
        evaluation.setUpdatedAt(now);

        Evaluation saved = evaluationRepository.save(evaluation);
        logger.info("Evaluation enregistree avec succes : ID={}", saved.getId());

        // 6. Publier EvaluationCreated dans RabbitMQ
        EvaluationCreatedEvent event = new EvaluationCreatedEvent(
                saved.getId(),
                saved.getAppointmentId(),
                saved.getProviderId(),
                saved.getRating()
        );
        evaluationEventPublisher.publishEvaluationCreated(event);

        // 7. Retourner l'evaluation creee (le controleur retournera HTTP 201)
        return EvaluationResponse.fromEntity(saved);
    }

    /**
     * Afficher une evaluation par ID
     */
    public EvaluationResponse getEvaluationById(String id) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Évaluation introuvable avec l'ID : " + id));
        return EvaluationResponse.fromEntity(evaluation);
    }

    /**
     * Afficher les evaluations d'un medecin
     */
    public List<EvaluationResponse> getEvaluationsByProviderId(String providerId) {
        List<Evaluation> evaluations = evaluationRepository.findByProviderId(providerId);
        return evaluations.stream()
                .map(EvaluationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Modifier une evaluation
     */
    public EvaluationResponse updateEvaluation(String id, EvaluationRequest request) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Évaluation introuvable avec l'ID : " + id));

        if (request.getRating() != null) {
            evaluation.setRating(request.getRating());
        }
        if (request.getComment() != null) {
            evaluation.setComment(request.getComment());
        }
        evaluation.setUpdatedAt(new Date());

        Evaluation updated = evaluationRepository.save(evaluation);
        logger.info("Evaluation modifiee avec succes : ID={}", id);
        return EvaluationResponse.fromEntity(updated);
    }

    /**
     * Supprimer une evaluation
     */
    public void deleteEvaluation(String id) {
        if (!evaluationRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Évaluation introuvable avec l'ID : " + id);
        }
        evaluationRepository.deleteById(id);
        logger.info("Evaluation supprimee avec succes : ID={}", id);
    }

    /**
     * Calculer la moyenne des evaluations d'un medecin
     */
    public Map<String, Object> calculateAverageRating(String providerId) {
        List<Evaluation> evaluations = evaluationRepository.findByProviderId(providerId);

        double average = 0.0;
        int total = evaluations.size();

        if (total > 0) {
            double sum = evaluations.stream()
                    .filter(e -> e.getRating() != null)
                    .mapToInt(Evaluation::getRating)
                    .sum();
            long count = evaluations.stream().filter(e -> e.getRating() != null).count();
            if (count > 0) {
                average = Math.round((sum / count) * 10.0) / 10.0;
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("providerId", providerId);
        response.put("averageRating", average);
        response.put("totalEvaluations", total);
        return response;
    }
}
