package FlightBooking.strategies.pricing;

import java.util.List;

import FlightBooking.entities.FlightSeat;
import FlightBooking.strategies.PricingStrategy;

/** Fare = sum of the base price of each seat's class. */
public class StandardPricingStrategy implements PricingStrategy {
    @Override
    public double calculateFare(List<FlightSeat> seats) {
        double total = 0.0;
        for (FlightSeat fs : seats) {
            total += fs.getSeat().getSeatClass().getBasePrice();
        }
        return total;
    }
}
