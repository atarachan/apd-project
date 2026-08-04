package ca.senecacollege.malibuluminahotel.decorators;

import java.math.BigDecimal;

/**
 * Concrete Decorator for Wi-Fi add-on service.
 * Wraps a booking component and adds the cost and description of Wi-Fi service.
 */
public class WifiDecorator extends AddOnDecorator {

    private final BigDecimal wifiCost;

    /**
     * Create a Wi-Fi decorator.
     * 
     * @param booking  the booking to decorate
     * @param wifiCost the total cost for Wi-Fi (price * nights)
     */
    public WifiDecorator(BookingComponent booking, BigDecimal wifiCost) {
        super(booking);
        this.wifiCost = wifiCost;
    }

    @Override
    protected BigDecimal getAddOnCost() {
        return wifiCost;
    }

    @Override
    protected String getAddOnDescription() {
        return "Wi-Fi";
    }
}
