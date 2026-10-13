package FlightBooking.strategies.pricing;

import java.util.List;

import FlightBooking.entities.FlightSeat;
import FlightBooking.strategies.PricingStrategy;

/** Same as standard pricing, plus a 20% peak-season surcharge. */
public class PeakSeasonPricingStrategy implements PricingStrategy {
    private static final double SURCHARGE_PERCENT = 20.0;

    @Override
    public double calculateFare(List<FlightSeat> seats) {
        double total = 0.0;
        for (FlightSeat fs : seats) {
            total += fs.getSeat().getSeatClass().getBasePrice();
        }
        return total + (total / 100.0) * SURCHARGE_PERCENT;
    }
}
