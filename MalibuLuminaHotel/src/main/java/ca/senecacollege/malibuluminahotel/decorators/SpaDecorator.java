package ca.senecacollege.malibuluminahotel.decorators;

import java.math.BigDecimal;

/**
 * Concrete Decorator for spa add-on service.
 * Wraps a booking component and adds the cost and description of spa package.
 */
public class SpaDecorator extends AddOnDecorator {

    private final BigDecimal spaCost;

    /**
     * Create a spa decorator.
     * 
     * @param booking the booking to decorate
     * @param spaCost the total cost for spa package (typically per-stay, not
     *                per-night)
     */
    public SpaDecorator(BookingComponent booking, BigDecimal spaCost) {
        super(booking);
        this.spaCost = spaCost;
    }

    @Override
    protected BigDecimal getAddOnCost() {
        return spaCost;
    }

    @Override
    protected String getAddOnDescription() {
        return "Spa Package";
    }
}
