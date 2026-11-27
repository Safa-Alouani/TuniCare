package com.tunicare.evaluation_service.messaging;

import com.tunicare.evaluation_service.config.RabbitMQConfig;
import com.tunicare.evaluation_service.messaging.event.EvaluationCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Publie les evenements d'evaluation vers RabbitMQ (destines a Provider-Service pour MAJ des stats medecin).
 * 
 * Note de fiabilite / Resilience :
 * En cas d'indisponibilite temporaire de RabbitMQ, l'exception est interceptee et journalisee
 * pour eviter de faire echouer la creation de l'evaluation dans Firestore.
 * Pour une production a haute tolerance aux pannes, un pattern Transactional Outbox
 * est recommande pour garantir une livraison a 100%.
 */
@Component
public class EvaluationEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(EvaluationEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name:" + RabbitMQConfig.EXCHANGE + "}")
    private String exchangeName;

    @Value("${rabbitmq.routing-key.evaluation-created:" + RabbitMQConfig.EVALUATION_CREATED_ROUTING_KEY + "}")
    private String evaluationCreatedRoutingKey;

    @Autowired
    public EvaluationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishEvaluationCreated(EvaluationCreatedEvent event) {
        try {
            logger.info("Publication de EvaluationCreated: exchange={}, routingKey={}, event={}",
                    exchangeName, evaluationCreatedRoutingKey, event);
            rabbitTemplate.convertAndSend(exchangeName, evaluationCreatedRoutingKey, event);
            logger.info("Evenement EvaluationCreated publie avec succes pour evaluationId={}", event.getEvaluationId());
        } catch (AmqpException e) {
            logger.error("Echec de publication de l'evenement EvaluationCreated sur RabbitMQ: {}. " +
                    "A prevoir: pattern Outbox pour rejeu ulterieur.", e.getMessage(), e);
        }
    }
}
