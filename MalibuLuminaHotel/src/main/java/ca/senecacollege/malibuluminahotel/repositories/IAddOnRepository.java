package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.AddOn;
import java.util.Optional;

public interface IAddOnRepository extends IRepository<AddOn, Long> {

    Optional<AddOn> findByName(String name);
}
