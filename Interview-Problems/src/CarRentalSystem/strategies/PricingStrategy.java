package CarRentalSystem.strategies;

import java.time.LocalDate;

import CarRentalSystem.entities.Vehicle;

public interface PricingStrategy {
    public double calculate(Vehicle vehicle, LocalDate start, LocalDate end);
}