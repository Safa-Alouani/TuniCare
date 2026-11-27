package com.tunicare.evaluation_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class EvaluationRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "L'identifiant du rendez-vous est obligatoire")
    private String appointmentId;

    @NotNull(message = "La note est obligatoire")
    @Min(value = 1, message = "La note minimale est 1")
    @Max(value = 5, message = "La note maximale est 5")
    private Integer rating;

    private String comment;

    // Optionnels dans le body (peuvent etre deduits automatiquement de Appointment-Service)
    private String patientId;
    private String providerId;

    public EvaluationRequest() {
    }

    public EvaluationRequest(String appointmentId, Integer rating, String comment) {
        this.appointmentId = appointmentId;
        this.rating = rating;
        this.comment = comment;
    }

    public EvaluationRequest(String appointmentId, String patientId, String providerId, Integer rating, String comment) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.providerId = providerId;
        this.rating = rating;
        this.comment = comment;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    @Override
    public String toString() {
        return "EvaluationRequest{" +
                "appointmentId='" + appointmentId + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", patientId='" + patientId + '\'' +
                ", providerId='" + providerId + '\'' +
                '}';
    }
}
