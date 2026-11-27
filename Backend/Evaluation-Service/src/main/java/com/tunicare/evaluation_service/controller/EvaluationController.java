package com.tunicare.evaluation_service.controller;

import com.tunicare.evaluation_service.dto.EvaluationRequest;
import com.tunicare.evaluation_service.dto.EvaluationResponse;
import com.tunicare.evaluation_service.service.EvaluationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/evaluations")
@CrossOrigin(origins = "*")
public class EvaluationController {

    private final EvaluationService evaluationService;

    @Autowired
    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    /**
     * Ajouter une evaluation
     * POST /api/evaluations
     */
    @PostMapping
    public ResponseEntity<EvaluationResponse> createEvaluation(@Valid @RequestBody EvaluationRequest request) {
        EvaluationResponse response = evaluationService.createEvaluation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Afficher une evaluation par son identifiant
     * GET /api/evaluations/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<EvaluationResponse> getEvaluationById(@PathVariable("id") String id) {
        EvaluationResponse response = evaluationService.getEvaluationById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Afficher les evaluations d'un medecin / prestataire
     * GET /api/evaluations/provider/{id}
     */
    @GetMapping("/provider/{id}")
    public ResponseEntity<List<EvaluationResponse>> getEvaluationsByProviderId(@PathVariable("id") String providerId) {
        List<EvaluationResponse> responses = evaluationService.getEvaluationsByProviderId(providerId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Modifier une evaluation
     * PUT /api/evaluations/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<EvaluationResponse> updateEvaluation(@PathVariable("id") String id,
                                                               @RequestBody EvaluationRequest request) {
        EvaluationResponse response = evaluationService.updateEvaluation(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Supprimer une evaluation
     * DELETE /api/evaluations/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvaluation(@PathVariable("id") String id) {
        evaluationService.deleteEvaluation(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Calculer la moyenne des evaluations d'un medecin
     * GET /api/evaluations/provider/{id}/average
     */
    @GetMapping("/provider/{id}/average")
    public ResponseEntity<Map<String, Object>> getAverageRating(@PathVariable("id") String providerId) {
        Map<String, Object> result = evaluationService.calculateAverageRating(providerId);
        return ResponseEntity.ok(result);
    }
}
