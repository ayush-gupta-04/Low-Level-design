package FlightBooking.strategies;

import java.util.List;

import FlightBooking.entities.FlightSeat;

public interface PricingStrategy {
    double calculateFare(List<FlightSeat> seats);
}
