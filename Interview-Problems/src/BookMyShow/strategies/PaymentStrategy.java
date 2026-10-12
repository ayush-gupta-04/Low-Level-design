package BookMyShow.strategies;

import BookMyShow.entities.Payment;

public interface PaymentStrategy {
    public Payment pay(double amount);
}
