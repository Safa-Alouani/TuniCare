package com.tunicare.evaluation_service.messaging.event;

import java.io.Serializable;

public class AppointmentCompletedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String appointmentId;
    private String patientId;
    private String providerId;

    public AppointmentCompletedEvent() {
    }

    public AppointmentCompletedEvent(String appointmentId, String patientId, String providerId) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.providerId = providerId;
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

    @Override
    public String toString() {
        return "AppointmentCompletedEvent{" +
                "appointmentId='" + appointmentId + '\'' +
                ", patientId='" + patientId + '\'' +
                ", providerId='" + providerId + '\'' +
                '}';
    }
}
