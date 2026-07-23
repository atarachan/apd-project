package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.Guest;
import java.util.List;
import java.util.Optional;

public interface IGuestRepository extends IRepository<Guest, Long> {

    Optional<Guest> findByEmail(String email);

    List<Guest> findByLastName(String lastName);
}
