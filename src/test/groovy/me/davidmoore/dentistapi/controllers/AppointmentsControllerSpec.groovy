package me.davidmoore.dentistapi.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import me.davidmoore.dentistapi.config.MockConfig
import me.davidmoore.dentistapi.models.Appointment
import me.davidmoore.dentistapi.repositories.AppointmentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@AutoConfigureMockMvc
@WebMvcTest(AppointmentsController.class)
@ContextConfiguration(classes = MockConfig.class)
class AppointmentsControllerSpec extends Specification {

    @SuppressWarnings("unused")
    @MockBean
    private AppointmentRepository appointmentRepository

    @Autowired
    private MockMvc mvc

    private static ObjectMapper objectMapper

    def setupSpec() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME

        LocalDateTimeDeserializer dateTimeDeserializer = new LocalDateTimeDeserializer(formatter)
        LocalDateTimeSerializer dateTimeSerializer = new LocalDateTimeSerializer(formatter)

        JavaTimeModule javaTimeModule = new JavaTimeModule()
        javaTimeModule.addDeserializer(LocalDateTime.class, dateTimeDeserializer)
        javaTimeModule.addSerializer(LocalDateTime.class, dateTimeSerializer)

        objectMapper = new ObjectMapper()
        objectMapper.registerModule(javaTimeModule)
    }

    def "CreateAppointment succeeds only with valid values"(LocalDateTime now, LocalDateTime startTime, LocalDateTime endTime, int expectedResponseCode) {
        given: "am appointment request"
            def rawAppointment = new Appointment(startTime, endTime, 1,1)

        expect: "appointment creation succeeds when valid"
            mvc.perform(MockMvcRequestBuilders
                    .post("/dentalAppointments/")
                    .accept(MediaType.APPLICATION_JSON)
                    .content(asJsonString(rawAppointment))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().is(expectedResponseCode))
        where:
            now                                                 | startTime            | endTime                   | expectedResponseCode
            LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES) | now.minusMinutes(30) | startTime.plusMinutes(30) | 400 //start time can't be in past
            LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES) | now.plusMinutes(30)  | now.minusMinutes(30)      | 400 // end time can't be before start or in past
            LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES) | now.plusMinutes(30)  | startTime.plusMinutes(30) | 200 // 30 min appointment in future is valid
            LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES) | now.plusMinutes(30)  | startTime.plusMinutes(10) | 400 // <30 min appt is invalid
            // Note: we don't test conflicting appointments because that is done at the data later and this is just testing the controller

    }

    // Note: we don't test get b/c it goes straight to the data layer

    String asJsonString(final Appointment appointment) {
        try {
            return objectMapper.writeValueAsString(appointment)
        } catch (Exception e) {
            throw new RuntimeException(e)
        }
    }

}
