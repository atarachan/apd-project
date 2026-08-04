package ca.senecacollege.malibuluminahotel.services;

import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.LoyaltyAccount;
import ca.senecacollege.malibuluminahotel.models.LoyaltyTransaction;
import ca.senecacollege.malibuluminahotel.models.enums.LoyaltyTransactionType;
import ca.senecacollege.malibuluminahotel.repositories.ILoyaltyAccountRepository;
import ca.senecacollege.malibuluminahotel.repositories.LoyaltyAccountRepositoryImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Service for managing loyalty program operations.
 */
public class LoyaltyService {

    private static final Logger LOGGER = Logger.getLogger(LoyaltyService.class.getName());
    private static final int POINTS_PER_DOLLAR = 10; // 10 points per $1 spent
    private static final BigDecimal POINT_VALUE = new BigDecimal("0.01"); // $0.01 per point

    private final ILoyaltyAccountRepository loyaltyAccountRepository;

    public LoyaltyService() {
        this.loyaltyAccountRepository = new LoyaltyAccountRepositoryImpl();
    }

    /**
     * Enrolls a guest in the loyalty program.
     */
    public LoyaltyAccount enrollGuest(Guest guest) {
        if (guest == null) {
            throw new IllegalArgumentException("Guest cannot be null");
        }

        // Check if already enrolled
        Optional<LoyaltyAccount> existing = loyaltyAccountRepository.findByGuest(guest);
        if (existing.isPresent()) {
            LOGGER.warning("Guest " + guest.getGuestId() + " is already enrolled in loyalty program");
            return existing.get();
        }

        // Generate unique member number
        String memberNumber = generateMemberNumber(guest);

        // Create new loyalty account
        LoyaltyAccount account = new LoyaltyAccount(guest, memberNumber);
        account = loyaltyAccountRepository.save(account);

        LOGGER.info("Guest " + guest.getGuestId() + " enrolled in loyalty program with member number: " + memberNumber);
        return account;
    }

    /**
     * Awards points based on payment amount.
     */
    public void earnPoints(LoyaltyAccount account, BigDecimal paymentAmount) {
        if (account == null) {
            throw new IllegalArgumentException("Loyalty account cannot be null");
        }
        if (paymentAmount == null || paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }

        // Calculate points earned (10 points per $1)
        int pointsEarned = paymentAmount.intValue() * POINTS_PER_DOLLAR;

        // Create transaction
        LoyaltyTransaction transaction = new LoyaltyTransaction();
        transaction.setLoyaltyAccount(account);
        transaction.setType(LoyaltyTransactionType.EARN);
        transaction.setPoints(pointsEarned);
        transaction.setTransactionDate(LocalDateTime.now());

        // Update account
        account.setCurrentPoints(account.getCurrentPoints() + pointsEarned);
        account.addTransaction(transaction);
        loyaltyAccountRepository.save(account);

        LOGGER.info("Awarded " + pointsEarned + " points to member " + account.getMemberNumber());
    }

    /**
     * Redeems points for payment credit.
     * Returns the dollar amount credited.
     */
    public BigDecimal redeemPoints(LoyaltyAccount account, int pointsToRedeem) {
        if (account == null) {
            throw new IllegalArgumentException("Loyalty account cannot be null");
        }
        if (pointsToRedeem <= 0) {
            throw new IllegalArgumentException("Points to redeem must be positive");
        }
        if (account.getCurrentPoints() < pointsToRedeem) {
            throw new IllegalArgumentException(
                    "Insufficient points. Available: " + account.getCurrentPoints() + ", Requested: " + pointsToRedeem);
        }

        // Calculate dollar value ($0.01 per point)
        BigDecimal creditAmount = POINT_VALUE.multiply(new BigDecimal(pointsToRedeem));

        // Create transaction
        LoyaltyTransaction transaction = new LoyaltyTransaction();
        transaction.setLoyaltyAccount(account);
        transaction.setType(LoyaltyTransactionType.REDEEM);
        transaction.setPoints(-pointsToRedeem); // Negative for redemption
        transaction.setTransactionDate(LocalDateTime.now());

        // Update account
        account.setCurrentPoints(account.getCurrentPoints() - pointsToRedeem);
        account.addTransaction(transaction);
        loyaltyAccountRepository.save(account);

        LOGGER.info("Redeemed " + pointsToRedeem + " points from member " + account.getMemberNumber() + " for $"
                + creditAmount);
        return creditAmount;
    }

    /**
     * Gets loyalty account for a guest.
     */
    public Optional<LoyaltyAccount> getAccountByGuest(Guest guest) {
        if (guest == null) {
            return Optional.empty();
        }
        return loyaltyAccountRepository.findByGuest(guest);
    }

    /**
     * Gets loyalty account by member number.
     */
    public Optional<LoyaltyAccount> getAccountByMemberNumber(String memberNumber) {
        if (memberNumber == null || memberNumber.trim().isEmpty()) {
            return Optional.empty();
        }
        return loyaltyAccountRepository.findByMemberNumber(memberNumber);
    }

    /**
     * Checks if a guest is enrolled in the loyalty program.
     */
    public boolean isEnrolled(Guest guest) {
        return getAccountByGuest(guest).isPresent();
    }

    /**
     * Generates a unique member number for a guest.
     */
    private String generateMemberNumber(Guest guest) {
        // Format: ML + guest ID padded to 8 digits
        return String.format("ML%08d", guest.getGuestId());
    }
}
