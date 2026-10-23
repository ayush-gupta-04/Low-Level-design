package ParkingLot.strategy;

import ParkingLot.entities.Payment;

public interface PaymentStrategy {
    public Payment pay(double amount);
}