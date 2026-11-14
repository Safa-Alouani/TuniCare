package com.tunicare.evaluation_service.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.Optional;

/**
 * Client REST pour interagir avec Appointment-Service (Fatma).
 * Appelle l'endpoint : GET http://localhost:PORT_APPOINTMENT/api/appointments/{id}
 */
@Component
public class AppointmentClient {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentClient.class);

    private final RestTemplate restTemplate;

    @Value("${services.appointment.url:http://localhost:8081}")
    private String appointmentServiceUrl;

    @Autowired
    public AppointmentClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * DTO representant la reponse attendue de Appointment-Service
     */
    public static class AppointmentDto implements Serializable {
        private static final long serialVersionUID = 1L;

        private String id;
        private String patientId;
        private String providerId;
        private String status;

        public AppointmentDto() {
        }

        public AppointmentDto(String id, String patientId, String providerId, String status) {
            this.id = id;
            this.patientId = patientId;
            this.providerId = providerId;
            this.status = status;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPatientId() {
            return patientId;
        }

        public void setPatientId(String patientId) {
            this.patientId = patientId;
        }

        public String getProviderId() {
            return providerId;
        }

        public void setProviderId(String providerId) {
            this.providerId = providerId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "AppointmentDto{" +
                    "id='" + id + '\'' +
                    ", patientId='" + patientId + '\'' +
                    ", providerId='" + providerId + '\'' +
                    ", status='" + status + '\'' +
                    '}';
        }
    }

    /**
     * Recupere un rendez-vous par son ID aupres de Appointment-Service
     */
    public Optional<AppointmentDto> getAppointmentById(String appointmentId) {
        String url = appointmentServiceUrl + "/api/appointments/" + appointmentId;
        logger.info("Appel REST vers Appointment-Service : {}", url);

        try {
            ResponseEntity<AppointmentDto> response = restTemplate.getForEntity(url, AppointmentDto.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                logger.info("Rendez-vous recupere avec succes : {}", response.getBody());
                return Optional.of(response.getBody());
            }
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Rendez-vous introuvable (404) pour l'id : {}", appointmentId);
            return Optional.empty();
        } catch (ResourceAccessException e) {
            logger.error("Impossible de contacter Appointment-Service sur {} (service non demarre ou mauvais port). Erreur : {}",
                    url, e.getMessage());
            throw new IllegalStateException("Appointment-Service inaccessible sur " + appointmentServiceUrl +
                    ". Verifier le port et l'endpoint avec Fatma.", e);
        } catch (Exception e) {
            logger.error("Erreur lors de l'appel a Appointment-Service pour l'id {} : {}", appointmentId, e.getMessage());
            throw new RuntimeException("Erreur de communication avec Appointment-Service : " + e.getMessage(), e);
        }

        return Optional.empty();
    }
}
