package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.Reservation;
import ca.senecacollege.malibuluminahotel.models.ReservationItem;
import ca.senecacollege.malibuluminahotel.models.Room;
import java.util.List;
import java.util.Optional;

public interface IReservationItemRepository extends IRepository<ReservationItem, Long> {

    List<ReservationItem> findByReservation(
            Reservation reservation
    );

    List<ReservationItem> findByRoom(
            Room room
    );

    Optional<ReservationItem> findByReservationAndRoom(
            Reservation reservation,
            Room room
    );
}
