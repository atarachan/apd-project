package ca.senecacollege.malibuluminahotel.services;

import ca.senecacollege.malibuluminahotel.models.RoomType;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class WeekendPricingStrategy implements PricingStrategy {

    private static final BigDecimal WEEKEND_MULTIPLIER = new BigDecimal("1.25");

    @Override
    public BigDecimal calculateNightlyRate(RoomType roomType, LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();

        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return roomType.getBaseRate().multiply(WEEKEND_MULTIPLIER);
        }

        return roomType.getBaseRate();
    }
}
