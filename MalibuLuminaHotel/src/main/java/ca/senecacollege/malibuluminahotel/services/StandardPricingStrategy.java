package ca.senecacollege.malibuluminahotel.services;

import ca.senecacollege.malibuluminahotel.models.RoomType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculateNightlyRate(RoomType roomType, LocalDate date) {
        return roomType.getBaseRate();
    }
}
