package com.tunicare.evaluation_service.messaging;

import com.tunicare.evaluation_service.config.RabbitMQConfig;
import com.tunicare.evaluation_service.messaging.event.AppointmentCompletedEvent;
import com.tunicare.evaluation_service.service.EvaluationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Ecoute les evenements AppointmentCompleted emis par Appointment-Service (Fatma).
 * Enregistre l'eligibilite du patient pour l'evaluation de ce rendez-vous termine.
 */
@Component
public class AppointmentEventListener {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentEventListener.class);

    private final EvaluationService evaluationService;

    @Autowired
    public AppointmentEventListener(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.appointment-completed:" + RabbitMQConfig.APPOINTMENT_COMPLETED_QUEUE + "}")
    public void handleAppointmentCompleted(AppointmentCompletedEvent event) {
        logger.info("Reception de l'evenement AppointmentCompleted : {}", event);

        if (event == null || event.getAppointmentId() == null) {
            logger.warn("Evenement AppointmentCompleted invalide recu (null ou appointmentId manquant)");
            return;
        }

        try {
            evaluationService.registerEligibility(event.getAppointmentId(), event.getPatientId(), event.getProviderId());
            logger.info("Eligibilite enregistree avec succes pour appointmentId={}, patientId={}",
                    event.getAppointmentId(), event.getPatientId());
        } catch (Exception e) {
            logger.error("Erreur lors de l'enregistrement de l'eligibilite pour appointmentId={} : {}",
                    event.getAppointmentId(), e.getMessage(), e);
        }
    }
}
