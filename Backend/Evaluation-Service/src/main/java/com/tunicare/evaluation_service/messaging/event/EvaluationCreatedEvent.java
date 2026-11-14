package com.tunicare.evaluation_service.messaging.event;

import java.io.Serializable;

public class EvaluationCreatedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String evaluationId;
    private String appointmentId;
    private String providerId;
    private Integer rating;

    public EvaluationCreatedEvent() {
    }

    public EvaluationCreatedEvent(String evaluationId, String appointmentId, String providerId, Integer rating) {
        this.evaluationId = evaluationId;
        this.appointmentId = appointmentId;
        this.providerId = providerId;
        this.rating = rating;
    }

    public String getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(String evaluationId) {
        this.evaluationId = evaluationId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "EvaluationCreatedEvent{" +
                "evaluationId='" + evaluationId + '\'' +
                ", appointmentId='" + appointmentId + '\'' +
                ", providerId='" + providerId + '\'' +
                ", rating=" + rating +
                '}';
    }
}
