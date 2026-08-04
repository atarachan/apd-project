package ca.senecacollege.malibuluminahotel.services;

import ca.senecacollege.malibuluminahotel.models.AdminUser;
import ca.senecacollege.malibuluminahotel.models.Bill;
import ca.senecacollege.malibuluminahotel.models.enums.UserRole;
import ca.senecacollege.malibuluminahotel.repositories.IBillRepository;
import com.google.inject.Inject;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Service for applying role-based discounts to bills.
 * ADMIN role: max 15% discount
 * MANAGER role: max 30% discount
 */
public class DiscountService implements IDiscountService {

    private static final BigDecimal ADMIN_MAX_DISCOUNT_PCT = new BigDecimal("15");
    private static final BigDecimal MANAGER_MAX_DISCOUNT_PCT = new BigDecimal("30");
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final IBillRepository billRepository;

    @Inject
    public DiscountService(IBillRepository billRepository) {
        this.billRepository = billRepository;
    }

    /**
     * Calculate discount amount based on percentage and role cap.
     *
     * @param subtotal   Bill subtotal
     * @param percentage Discount percentage (e.g., 10 for 10%)
     * @param adminUser  Admin user applying the discount
     * @return Discount amount
     * @throws IllegalArgumentException if percentage exceeds role cap
     */
    @Override
    public BigDecimal calculateDiscount(BigDecimal subtotal, double percentage, AdminUser adminUser) {
        BigDecimal discountPct = new BigDecimal(Double.toString(percentage));

        // Validate against role cap
        BigDecimal maxPct = getMaxDiscountPercentage(adminUser.getRole());
        if (discountPct.compareTo(maxPct) > 0) {
            throw new IllegalArgumentException(
                    "Discount percentage " + percentage + "% exceeds " +
                            adminUser.getRole() + " role limit of " + maxPct + "%");
        }

        // Calculate discount amount
        return subtotal.multiply(discountPct)
                .divide(HUNDRED, 2, RoundingMode.HALF_UP);
    }

    /**
     * Apply discount to a bill.
     *
     * @param bill       Bill to apply discount to
     * @param percentage Discount percentage
     * @param adminUser  Admin user applying the discount
     * @throws IllegalArgumentException if percentage exceeds role cap
     */
    @Override
    public void applyDiscount(Bill bill, double percentage, AdminUser adminUser) {
        BigDecimal discountAmount = calculateDiscount(bill.getSubtotal(), percentage, adminUser);

        // Update bill discount
        bill.setDiscount(discountAmount);

        // Recalculate total: subtotal - discount + tax
        BigDecimal newTotal = bill.getSubtotal()
                .subtract(discountAmount)
                .add(bill.getTax());
        bill.setTotal(newTotal);
        bill.setBalanceDue(newTotal);

        // Save updated bill
        billRepository.save(bill);
    }

    /**
     * Get maximum discount percentage for a role.
     */
    @Override
    public BigDecimal getMaxDiscountPercentage(UserRole role) {
        return switch (role) {
            case ADMIN -> ADMIN_MAX_DISCOUNT_PCT;
            case MANAGER -> MANAGER_MAX_DISCOUNT_PCT;
        };
    }

    /**
     * Validate if a discount percentage is allowed for a role.
     */
    @Override
    public boolean isDiscountAllowed(double percentage, UserRole role) {
        BigDecimal discountPct = new BigDecimal(Double.toString(percentage));
        BigDecimal maxPct = getMaxDiscountPercentage(role);
        return discountPct.compareTo(maxPct) <= 0;
    }
}
