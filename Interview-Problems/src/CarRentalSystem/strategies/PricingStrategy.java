package CarRentalSystem.strategies;

import CarRentalSystem.entities.Vehicle;

public interface PricingStrategy {
    public double calculate(Vehicle vehicle, long days);
}