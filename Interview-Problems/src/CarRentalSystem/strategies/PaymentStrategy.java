package CarRentalSystem.strategies;

import CarRentalSystem.entities.Payment;

public interface PaymentStrategy {
    public Payment pay(String billId, double amount);
}
