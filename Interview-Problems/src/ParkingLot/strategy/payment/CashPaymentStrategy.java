package ParkingLot.strategy.payment;

import ParkingLot.entities.Payment;
import ParkingLot.enums.PaymentMethod;
import ParkingLot.enums.PaymentStatus;
import ParkingLot.strategy.PaymentStrategy;

public class CashPaymentStrategy implements PaymentStrategy {
    @Override
    public Payment pay(double amount) {
        System.out.println("Collecting cash - " + amount);
        return new Payment(PaymentMethod.CASH, PaymentStatus.SUCCESS, amount);
    }
}