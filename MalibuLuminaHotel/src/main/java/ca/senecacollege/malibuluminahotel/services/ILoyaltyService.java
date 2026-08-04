package ca.senecacollege.malibuluminahotel.services;

import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.LoyaltyAccount;

import java.math.BigDecimal;
import java.util.Optional;

public interface ILoyaltyService {

    LoyaltyAccount enrollGuest(Guest guest);

    void earnPoints(LoyaltyAccount account, BigDecimal paymentAmount);

    BigDecimal redeemPoints(LoyaltyAccount account, int pointsToRedeem);

    Optional<LoyaltyAccount> getAccountByGuest(Guest guest);

    Optional<LoyaltyAccount> getAccountByMemberNumber(String memberNumber);

    boolean isEnrolled(Guest guest);
}
