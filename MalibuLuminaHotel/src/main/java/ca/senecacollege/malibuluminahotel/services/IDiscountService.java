package ca.senecacollege.malibuluminahotel.services;

import ca.senecacollege.malibuluminahotel.models.AdminUser;
import ca.senecacollege.malibuluminahotel.models.Bill;
import ca.senecacollege.malibuluminahotel.models.enums.UserRole;

import java.math.BigDecimal;

public interface IDiscountService {

    BigDecimal calculateDiscount(BigDecimal subtotal, double percentage, AdminUser adminUser);

    void applyDiscount(Bill bill, double percentage, AdminUser adminUser);

    BigDecimal getMaxDiscountPercentage(UserRole role);

    boolean isDiscountAllowed(double percentage, UserRole role);
}
