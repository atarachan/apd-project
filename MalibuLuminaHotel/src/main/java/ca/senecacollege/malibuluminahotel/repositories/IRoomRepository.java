package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.Room;
import ca.senecacollege.malibuluminahotel.models.RoomType;
import ca.senecacollege.malibuluminahotel.models.enums.RoomStatus;
import java.util.List;
import java.util.Optional;

public interface IRoomRepository extends IRepository<Room, Long> {

    Optional<Room> findByRoomNumber(String roomNumber);

    List<Room> findByStatus(RoomStatus status);

    List<Room> findByRoomType(RoomType roomType);

    List<Room> findByFloor(int floor);
}
