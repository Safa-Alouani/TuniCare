package com.tunicare.evaluation_service.dto;

import com.tunicare.evaluation_service.model.Evaluation;
import java.io.Serializable;
import java.util.Date;

public class EvaluationResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String appointmentId;
    private String patientId;
    private String providerId;
    private Integer rating;
    private String comment;
    private Date createdAt;
    private Date updatedAt;

    public EvaluationResponse() {
    }

    public EvaluationResponse(String id, String appointmentId, String patientId, String providerId,
                              Integer rating, String comment, Date createdAt, Date updatedAt) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.providerId = providerId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static EvaluationResponse fromEntity(Evaluation evaluation) {
        if (evaluation == null) {
            return null;
        }
        return new EvaluationResponse(
                evaluation.getId(),
                evaluation.getAppointmentId(),
                evaluation.getPatientId(),
                evaluation.getProviderId(),
                evaluation.getRating(),
                evaluation.getComment(),
                evaluation.getCreatedAt(),
                evaluation.getUpdatedAt()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "EvaluationResponse{" +
                "id='" + id + '\'' +
                ", appointmentId='" + appointmentId + '\'' +
                ", patientId='" + patientId + '\'' +
                ", providerId='" + providerId + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
