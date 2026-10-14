package CarRentalSystem.strategies.pricing;

import CarRentalSystem.entities.Vehicle;
import CarRentalSystem.strategies.PricingStrategy;

public class DailyPricing implements PricingStrategy {
    public double calculate(Vehicle vehicle, long days) {
        return vehicle.getPricePerDay() * days;
    }
}
