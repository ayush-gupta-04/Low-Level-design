package HotelBooking.strategies;

import java.util.UUID;

import HotelBooking.entities.Payment;
import HotelBooking.enums.PaymentStatus;

public class DebitCardPaymentStrategy implements PaymentStrategy {
    @Override
    public Payment pay(double amount) {
        System.out.println("Paying INR " + amount + " by debit card");
        PaymentStatus status = amount > 0 ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
        return new Payment(amount, status, "DEBIT_" + UUID.randomUUID());
    }
}
