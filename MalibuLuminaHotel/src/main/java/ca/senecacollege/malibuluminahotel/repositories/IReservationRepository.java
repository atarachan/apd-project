package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.Reservation;
import ca.senecacollege.malibuluminahotel.models.enums.ReservationStatus;
import java.time.LocalDate;
import java.util.List;

public interface IReservationRepository extends IRepository<Reservation, Long> {

    List<Reservation> findByGuest(Guest guest);

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByCheckInDate(LocalDate checkInDate);

    List<Reservation> findByDateRange(
            LocalDate startDate, LocalDate endDate
    );
}
