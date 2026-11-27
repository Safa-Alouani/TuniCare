package com.tunicare.evaluation_service.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "mediconnect.events";
    public static final String APPOINTMENT_COMPLETED_QUEUE = "evaluation.appointment.completed";
    public static final String APPOINTMENT_COMPLETED_ROUTING_KEY = "appointment.completed";
    public static final String EVALUATION_CREATED_ROUTING_KEY = "evaluation.created";

    @Value("${rabbitmq.exchange.name:" + EXCHANGE + "}")
    private String exchangeName;

    @Value("${rabbitmq.queue.appointment-completed:" + APPOINTMENT_COMPLETED_QUEUE + "}")
    private String appointmentCompletedQueueName;

    @Value("${rabbitmq.routing-key.appointment-completed:" + APPOINTMENT_COMPLETED_ROUTING_KEY + "}")
    private String appointmentCompletedRoutingKey;

    @Bean
    public TopicExchange eventExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Queue appointmentCompletedQueue() {
        return QueueBuilder.durable(appointmentCompletedQueueName).build();
    }

    @Bean
    public Binding appointmentCompletedBinding(Queue appointmentCompletedQueue, TopicExchange eventExchange) {
        return BindingBuilder.bind(appointmentCompletedQueue)
                .to(eventExchange)
                .with(appointmentCompletedRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
