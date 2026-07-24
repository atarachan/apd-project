package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.LoyaltyAccount;
import java.util.List;
import java.util.Optional;

public interface ILoyaltyAccountRepository
        extends IRepository<LoyaltyAccount, Long> {

    Optional<LoyaltyAccount> findByGuest(
            Guest guest
    );

    Optional<LoyaltyAccount> findByMemberNumber(
            String memberNumber
    );

    List<LoyaltyAccount> findByMinimumPoints(
            int minimumPoints
    );
}
