package ca.senecacollege.malibuluminahotel.decorators;

import java.math.BigDecimal;

/**
 * Concrete Component for the Decorator pattern.
 * Represents the base booking cost (room charges only, no add-ons).
 */
public class BaseBooking implements BookingComponent {

    private final BigDecimal roomCost;
    private final String roomDescription;

    /**
     * Create a base booking with room cost only.
     * 
     * @param roomCost        the total cost of the room(s) for the stay
     * @param roomDescription description of the room booking
     */
    public BaseBooking(BigDecimal roomCost, String roomDescription) {
        this.roomCost = roomCost;
        this.roomDescription = roomDescription;
    }

    @Override
    public BigDecimal getCost() {
        return roomCost;
    }

    @Override
    public String getDescription() {
        return roomDescription;
    }
}
