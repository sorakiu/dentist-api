package me.davidmoore.dentistapi.models;

import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Appointment {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @ApiModelProperty(position = 1, value = "Appointment ID", example = "1", hidden = true)
  private int id;

  @ApiModelProperty(position = 2, value = "start time of appointment in UTC, truncated to minutes",
      example = "2019-10-23T15:00:00Z")
  private LocalDateTime startTime;

  @ApiModelProperty(position = 3, value = "end time of appointment in UTC, truncated to minutes",
      example = "2019-10-23T15:30:00Z")
  private LocalDateTime endTime;

  @ApiModelProperty(position = 4, value = "Dentist ID", example = "1")
  private int dentistId;

  @ApiModelProperty(position = 5, value = "Patient ID", example = "1")
  private int patientId;

  @SuppressWarnings("unused")
  protected Appointment() {
  }

  @SuppressWarnings({"CheckStyle", "WeakerAccess"})
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

  @SuppressWarnings("unused")
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

  @SuppressWarnings("unused")
  public int getPatientId() {
    return patientId;
  }

  /**
   * truncate a raw appointment's start and end time to minutes.
   *
   * @return truncated appointment
   */
  public Appointment truncate() {
    return new Appointment(startTime.truncatedTo(ChronoUnit.MINUTES),
        endTime.truncatedTo(ChronoUnit.MINUTES),
        dentistId,
        patientId);
  }
}
