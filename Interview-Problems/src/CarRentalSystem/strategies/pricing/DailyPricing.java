package CarRentalSystem.strategies.pricing;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import CarRentalSystem.entities.Vehicle;
import CarRentalSystem.strategies.PricingStrategy;

public class DailyPricing implements PricingStrategy {
    public double calculate(Vehicle vehicle, LocalDate start, LocalDate end) {
        return vehicle.getPricePerDay() * Math.max(1, ChronoUnit.DAYS.between(start, end));
    }
}
