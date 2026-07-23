package ca.senecacollege.malibuluminahotel.services;

import ca.senecacollege.malibuluminahotel.models.RoomType;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PricingStrategy {

    BigDecimal calculateNightlyRate(RoomType roomType, LocalDate date);
}
