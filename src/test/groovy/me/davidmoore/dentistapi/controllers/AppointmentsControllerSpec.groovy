package me.davidmoore.dentistapi.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import me.davidmoore.dentistapi.config.MockConfig
import me.davidmoore.dentistapi.models.Appointment
import me.davidmoore.dentistapi.repositories.AppointmentRepository
import org.mockito.Mockito
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

import org.mockito.Mockito

@AutoConfigureMockMvc
@WebMvcTest(AppointmentsController.class)
@ContextConfiguration(classes = MockConfig.class)
class AppointmentsControllerSpec extends Specification {

    private static final Logger LOG = LoggerFactory.getLogger(AppointmentsControllerSpec.class)

    @MockBean
    private AppointmentRepository appointmentRepository

    @Autowired
    private MockMvc mvc

    private static ObjectMapper objectMapper

    def setupSpec() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME

        LocalDateTimeDeserializer dateTimeDeserializer = new LocalDateTimeDeserializer(formatter)
        LocalDateTimeSerializer dateTimeSerializer = new LocalDateTimeSerializer(formatter)

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class, dateTimeDeserializer);
        javaTimeModule.addSerializer(LocalDateTime.class, dateTimeSerializer);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(javaTimeModule);
    }

    def setup() {
    }


    def "CreateAppointment succeeds only with valid values"() {
        given: "a valid appointment"
            def now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
            def startTime = now.plusHours(1)
            def rawAppointment = new Appointment(startTime: startTime,
                    endTime: startTime.plusMinutes(30),
                    dentistId: 1,
                    patientId: 1
            )

        expect: "appointment creation succeeds when valid"
            mvc.perform(MockMvcRequestBuilders
                    .post("/dentalAppointments/")
                    .accept(MediaType.APPLICATION_JSON)
                    .content(asJsonString(rawAppointment))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())


    }

    String asJsonString(final Appointment appointment) {
        try {
            return objectMapper.writeValueAsString(appointment)
        } catch (Exception e) {
            throw new RuntimeException(e)
        }
    }

    def "GetAppointment"() {
    }


}
