package me.davidmoore.dentistapi.controllers;

import com.google.common.base.Preconditions;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import me.davidmoore.dentistapi.models.Appointment;
import me.davidmoore.dentistapi.repositories.AppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppointmentsController {

  private static final Logger LOG = LoggerFactory.getLogger(AppointmentsController.class);

  private final AppointmentRepository appointmentRepository;

  @Autowired
  public AppointmentsController(AppointmentRepository appointmentRepository) {
    this.appointmentRepository = appointmentRepository;
  }

  /**
   * creates appointments between a dentist and patient. The constraints are as follows: startTime
   * and endTime must both be valid times, and in the future A dental appointment should be at least
   * 30 minutes Two appointments for the same dentist can't start at the same time (appointments CAN
   * overlap, but 2 appointments for 1 dentist should not start at the exact same time
   *
   * @param requestedAppointment - appointment to validate and create
   * @return the created appointment or a descriptive error if it failed validation
   */
  @PostMapping(
      path = "/dentalAppointments",
      consumes = "application/json",
      produces = "application/json")
  public Appointment createAppointment(@RequestBody Appointment requestedAppointment) {
    LOG.info("create appointment: " + requestedAppointment);
    LocalDateTime now = LocalDateTime.now();

    // Validate constraints for new appointments
    Preconditions.checkArgument(requestedAppointment.getStartTime().isAfter(now),
        "Start time MUST be in the future.");
    Preconditions.checkArgument(requestedAppointment.getEndTime().isAfter(now),
        "End time MUST be in the future.");
    Preconditions.checkArgument(
        requestedAppointment.getEndTime().isAfter(requestedAppointment.getStartTime()),
        "End time MUST be after start time.");
    Preconditions.checkArgument(
        requestedAppointment.getStartTime()
            .until(requestedAppointment.getEndTime(), ChronoUnit.MINUTES) >= 30,
        "Minimum appointment duration is 30 minutes");
    Preconditions.checkArgument(
        appointmentRepository
            .findAllByDentistIdAndStartTime(
                requestedAppointment.getDentistId(), requestedAppointment.getStartTime())
            .isEmpty(),
        "The dentist you have requested already has an appointment scheduled at "
            + requestedAppointment.getStartTime());
    return appointmentRepository.save(requestedAppointment);
  }

  /**
   * Query database for an appointment.
   *
   * @param id - appointment ID
   * @return appointment if found, error otherwise
   */
  @GetMapping(path = "/dentalAppointments/{id}", produces = "application/json")
  public Appointment getAppointment(@RequestParam(name = "id") int id) {
    LOG.info("attempting to find appointment: {}", id);
    return appointmentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid appointment ID:" + id));
  }
}
