package ca.senecacollege.malibuluminahotel.decorators;

import java.math.BigDecimal;

/**
 * Concrete Decorator for breakfast add-on service.
 * Wraps a booking component and adds the cost and description of daily
 * breakfast.
 */
public class BreakfastDecorator extends AddOnDecorator {

    private final BigDecimal breakfastCost;

    /**
     * Create a breakfast decorator.
     * 
     * @param booking       the booking to decorate
     * @param breakfastCost the total cost for breakfast (price * nights)
     */
    public BreakfastDecorator(BookingComponent booking, BigDecimal breakfastCost) {
        super(booking);
        this.breakfastCost = breakfastCost;
    }

    @Override
    protected BigDecimal getAddOnCost() {
        return breakfastCost;
    }

    @Override
    protected String getAddOnDescription() {
        return "Daily Breakfast";
    }
}
