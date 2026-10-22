package CarRentalSystem.strategies;

import CarRentalSystem.entities.Payment;

public interface PaymentStrategy {
    public Payment pay(double amount);
}
