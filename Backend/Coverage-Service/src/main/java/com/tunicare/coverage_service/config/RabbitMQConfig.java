package com.tunicare.coverage_service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Le nom de notre file d'attente
    public static final String ELIGIBILITY_QUEUE = "eligibility_queue";

    // On demande à Spring Boot de créer cette file au démarrage
    @Bean
    public Queue eligibilityQueue() {
        return new Queue(ELIGIBILITY_QUEUE, false);
    }
}