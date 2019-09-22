package me.davidmoore.dentistapi.repositories;

import java.time.LocalDateTime;
import java.util.List;
import me.davidmoore.dentistapi.models.Appointment;
import org.springframework.data.repository.CrudRepository;

public interface AppointmentRepository extends CrudRepository<Appointment, Integer> {

  List<Appointment> findAllByDentistIdAndStartTime(int dentistId, LocalDateTime startTime);
}
