package ca.senecacollege.malibuluminahotel.decorators;

import java.math.BigDecimal;

/**
 * Concrete Decorator for parking add-on service.
 * Wraps a booking component and adds the cost and description of parking
 * service.
 */
public class ParkingDecorator extends AddOnDecorator {

    private final BigDecimal parkingCost;

    /**
     * Create a parking decorator.
     * 
     * @param booking     the booking to decorate
     * @param parkingCost the total cost for parking (price * nights)
     */
    public ParkingDecorator(BookingComponent booking, BigDecimal parkingCost) {
        super(booking);
        this.parkingCost = parkingCost;
    }

    @Override
    protected BigDecimal getAddOnCost() {
        return parkingCost;
    }

    @Override
    protected String getAddOnDescription() {
        return "Parking";
    }
}
