package me.davidmoore.dentistapi.controllers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.google.common.base.Preconditions;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import me.davidmoore.dentistapi.models.Appointment;
import me.davidmoore.dentistapi.repositories.AppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/dentalAppointments", produces = APPLICATION_JSON_VALUE)
public class AppointmentsController {

  private static final Logger LOG = LoggerFactory.getLogger(AppointmentsController.class);

  private final AppointmentRepository appointmentRepository;

  @Autowired
  public AppointmentsController(AppointmentRepository appointmentRepository) {
    this.appointmentRepository = appointmentRepository;
  }

  /**
   * creates appointments between a dentist and patient.
   * <p>The constraints are as follows:
   * <ul>
   * <li>1.) StartTime and endTime must both be valid times, and in the future.</li>
   * <li>2.) A dental appointment should be at least 30 minutes.</li>
   * <li>3.) Two appointments for the same dentist can't start at the same time. (appointments CAN
   *  overlap, but 2 appointments for 1 dentist should not start at the exact same time)</li>
   * </ul>
   * </p>
   *
   * @param rawAppointment - appointment to validate and create
   * @return the created appointment or a descriptive error if it failed validation
   */
  @PostMapping(
      path = "/")
  @ApiOperation(value = "Create an appointment with validation",
      notes = "<p>The constraints are as follows:\n"
          + "   <ul>\n"
          + "   <li>StartTime and endTime must both be valid times, and in the future.</li>\n"
          + "   <li>A dental appointment should be at least 30 minutes.</li>\n"
          + "   <li>Two appointments for the same dentist can't start at the same time. ("
          + "       appointments CAN overlap, but 2 appointments for 1 dentist should not start at "
          + "       the exact same time)</li>\n"
          + "   </ul>\n"
          + "   </p>")
  @ApiResponses(value = {@ApiResponse(code = 400, message = "Bad Request")})
  public Appointment createAppointment(
      @RequestBody
      @ApiParam(name = "requestedAppointment",
          value = "Requested appointment. All times will be truncated to minutes",
          required = true)
          Appointment rawAppointment) {
    LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    Appointment requestedAppointment = rawAppointment.truncate();

    LOG.info("create appointment: " + requestedAppointment);

    // Validate constraints for new appointments
    try {
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
    } catch (IllegalArgumentException e) {
      LOG.error("Validation error for requested appointment {}", requestedAppointment, e);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    return appointmentRepository.save(requestedAppointment);
  }

  /**
   * Query database for an appointment.
   *
   * @param id - appointment ID
   * @return appointment if found, error otherwise
   */
  @GetMapping(path = "/{id}", produces = "application/json")
  @ApiOperation(value = "Find appointment by ID.")
  @ApiResponses(value = {
      @ApiResponse(code = 404, message = "Appointment not found")}
  )
  public Appointment getAppointment(
      @ApiParam(value = "ID of appointment that needs to be fetched", required = true,
          example = "1")
      @PathVariable(name = "id") int id) {
    LOG.info("attempting to find appointment: {}", id);
    try {
      return appointmentRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid appointment ID:" + id));
    } catch (IllegalArgumentException e) {
      LOG.error("Error finding appointment {}", id, e);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment " + id + " not found");
    }
  }
}
