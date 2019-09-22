package me.davidmoore.dentistapi.models;

import java.time.LocalDateTime;


public class Appointment {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final int dentistId;
    private final int patientId;

    public Appointment(LocalDateTime startTime, LocalDateTime endTime, int dentistId, int patientId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.dentistId = dentistId;
        this.patientId = patientId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public int getDentistId() {
        return dentistId;
    }

    public int getPatientId() {
        return patientId;
    }
}
