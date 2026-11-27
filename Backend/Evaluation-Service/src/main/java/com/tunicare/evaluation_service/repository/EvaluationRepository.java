package com.tunicare.evaluation_service.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.tunicare.evaluation_service.model.Evaluation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Repository
public class EvaluationRepository {

    private static final Logger logger = LoggerFactory.getLogger(EvaluationRepository.class);
    private static final String COLLECTION_NAME = "evaluations";

    private final Firestore firestore;

    // In-memory fallback si Firestore n'est pas initialise ou hors ligne
    private final Map<String, Evaluation> inMemoryStore = new ConcurrentHashMap<>();

    @Autowired
    public EvaluationRepository(@Autowired(required = false) Firestore firestore) {
        this.firestore = firestore;
        if (this.firestore == null) {
            logger.warn("Client Firestore non disponible. Mode memoire active.");
        }
    }

    public Evaluation save(Evaluation evaluation) {
        if (evaluation.getId() == null || evaluation.getId().trim().isEmpty()) {
            evaluation.setId(UUID.randomUUID().toString());
        }

        if (firestore != null) {
            try {
                DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(evaluation.getId());
                ApiFuture<WriteResult> future = docRef.set(evaluation);
                future.get();
                logger.info("Evaluation enregistree dans Firestore avec ID : {}", evaluation.getId());
                return evaluation;
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Echec d'enregistrement Firestore, fallback memoire : {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        inMemoryStore.put(evaluation.getId(), evaluation);
        return evaluation;
    }

    public Optional<Evaluation> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }

        if (firestore != null) {
            try {
                DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
                ApiFuture<DocumentSnapshot> future = docRef.get();
                DocumentSnapshot document = future.get();
                if (document.exists()) {
                    Evaluation evaluation = document.toObject(Evaluation.class);
                    if (evaluation != null) {
                        evaluation.setId(document.getId());
                        return Optional.of(evaluation);
                    }
                }
                return Optional.empty();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Erreur de recuperation Firestore pour ID {}: {}", id, e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        return Optional.ofNullable(inMemoryStore.get(id));
    }

    public List<Evaluation> findByProviderId(String providerId) {
        List<Evaluation> results = new ArrayList<>();

        if (firestore != null) {
            try {
                ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                        .whereEqualTo("providerId", providerId)
                        .get();
                List<QueryDocumentSnapshot> documents = future.get().getDocuments();
                for (QueryDocumentSnapshot document : documents) {
                    Evaluation evaluation = document.toObject(Evaluation.class);
                    if (evaluation != null) {
                        evaluation.setId(document.getId());
                        results.add(evaluation);
                    }
                }
                return results;
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Erreur requete Firestore par providerId : {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        for (Evaluation evaluation : inMemoryStore.values()) {
            if (providerId != null && providerId.equals(evaluation.getProviderId())) {
                results.add(evaluation);
            }
        }
        return results;
    }

    public List<Evaluation> findAll() {
        List<Evaluation> results = new ArrayList<>();

        if (firestore != null) {
            try {
                ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
                List<QueryDocumentSnapshot> documents = future.get().getDocuments();
                for (QueryDocumentSnapshot document : documents) {
                    Evaluation evaluation = document.toObject(Evaluation.class);
                    if (evaluation != null) {
                        evaluation.setId(document.getId());
                        results.add(evaluation);
                    }
                }
                return results;
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Erreur requete Firestore findAll : {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        results.addAll(inMemoryStore.values());
        return results;
    }

    public Optional<Evaluation> findByAppointmentId(String appointmentId) {
        if (appointmentId == null) {
            return Optional.empty();
        }

        if (firestore != null) {
            try {
                ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                        .whereEqualTo("appointmentId", appointmentId)
                        .limit(1)
                        .get();
                List<QueryDocumentSnapshot> documents = future.get().getDocuments();
                if (!documents.isEmpty()) {
                    QueryDocumentSnapshot doc = documents.get(0);
                    Evaluation evaluation = doc.toObject(Evaluation.class);
                    if (evaluation != null) {
                        evaluation.setId(doc.getId());
                        return Optional.of(evaluation);
                    }
                }
                return Optional.empty();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Erreur requete Firestore par appointmentId : {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        return inMemoryStore.values().stream()
                .filter(e -> appointmentId.equals(e.getAppointmentId()))
                .findFirst();
    }

    public void deleteById(String id) {
        if (id == null) {
            return;
        }

        if (firestore != null) {
            try {
                DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
                docRef.delete().get();
                logger.info("Evaluation supprimee de Firestore avec ID : {}", id);
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Erreur suppression Firestore pour ID {}: {}", id, e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        inMemoryStore.remove(id);
    }

    public boolean existsById(String id) {
        if (id == null) {
            return false;
        }

        if (firestore != null) {
            try {
                DocumentSnapshot doc = firestore.collection(COLLECTION_NAME).document(id).get().get();
                return doc.exists();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Erreur verification existence Firestore pour ID {}: {}", id, e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        return inMemoryStore.containsKey(id);
    }
}
