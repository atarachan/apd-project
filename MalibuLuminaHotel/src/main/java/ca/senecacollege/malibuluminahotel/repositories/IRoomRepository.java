package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.Room;
import ca.senecacollege.malibuluminahotel.models.RoomType;
import ca.senecacollege.malibuluminahotel.models.enums.RoomStatus;
import ca.senecacollege.malibuluminahotel.models.enums.RoomTypeName;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IRoomRepository extends IRepository<Room, Long> {

    Optional<Room> findByRoomNumber(String roomNumber);

    List<Room> findByStatus(RoomStatus status);

    List<Room> findByRoomType(RoomType roomType);

    List<Room> findByFloor(int floor);

    // Returns the first room of the given type that has no overlapping reservation during the date range.
    Optional<Room> findFirstAvailable(RoomTypeName roomTypeName, LocalDate checkIn, LocalDate checkOut);
}
