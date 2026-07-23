package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.Room;
import ca.senecacollege.malibuluminahotel.models.enums.RoomTypeName;

import java.time.LocalDate;
import java.util.Optional;

public interface IRoomRepository extends IRepository<Room, Long> {

    Optional<Room> findFirstAvailable(RoomTypeName typeName, LocalDate checkIn, LocalDate checkOut);
}
