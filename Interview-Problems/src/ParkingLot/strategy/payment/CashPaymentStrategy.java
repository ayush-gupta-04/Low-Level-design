package ParkingLot.strategy.payment;

import ParkingLot.strategy.PaymentStrategy;

public class CashPaymentStrategy implements PaymentStrategy {
    @Override
    public boolean pay(double amount) {
        System.out.println("Collecting cash - " + amount);
        return true;
    }
}