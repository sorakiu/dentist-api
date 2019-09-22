package me.davidmoore.dentistapi.controllers;

import me.davidmoore.dentistapi.models.Appointment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppointmentsController {

    @RequestMapping("/dentalAppointments")
    @PostMapping(produces = "application")
    public Appointment createAppointment(@RequestBody Appointment appointmentCreate) {
        return appointmentCreate;
    }
}
