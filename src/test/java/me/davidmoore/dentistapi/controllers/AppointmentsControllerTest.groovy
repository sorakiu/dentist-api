package me.davidmoore.dentistapi.controllers

import me.davidmoore.dentistapi.models.Appointment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import java.time.LocalDateTime

@AutoConfigureMockMvc
@WebMvcTest
class AppointmentsControllerSpecification extends Specification {

    @Autowired
    private MockMvc mvc

    def "CreateAppointment succeeds only with valid values"() {
        given: "a valid appointment"
            def rawAppointment = new Appointment(startTime: LocalDateTime.now(),
            endTime: LocalDateTime.now().plusMinutes(30),
                    dentistId: 1,
                    patientId: 1
            )
        expect: "appointment creation succeeds when valid"
        mvc.perform(post("/dentalAppointments"))
        .andExpect(status().isOk())
        .andReturn()
        .response
        .getContentAsString() == "Hello world!"

    }

    def "GetAppointment"() {
    }
}
