package me.davidmoore.dentistapi.models;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Appointment {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private int dentistId;
  private int patientId;

  protected Appointment() {
  }

  public Appointment(LocalDateTime startTime, LocalDateTime endTime, int dentistId, int patientId) {
    this.startTime = startTime;
    this.endTime = endTime;
    this.dentistId = dentistId;
    this.patientId = patientId;
  }

  @Override
  public String toString() {
    return String.format("Appointment[id=%d, startTime=%s, endTime=%s, dentistId=%d, patientId=%d]",
        id, startTime, endTime, dentistId, patientId);
  }

  public int getId() {
    return id;
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
