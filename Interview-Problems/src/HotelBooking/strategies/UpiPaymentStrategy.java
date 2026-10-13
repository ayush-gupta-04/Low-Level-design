package HotelBooking.strategies;

import java.util.UUID;

import HotelBooking.entities.Payment;
import HotelBooking.enums.PaymentStatus;

public class UpiPaymentStrategy implements PaymentStrategy {
    @Override
    public Payment pay(double amount) {
        System.out.println("Paying INR " + amount + " by UPI");
        PaymentStatus status = amount > 0 ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
        return new Payment(amount, status, "UPI_" + UUID.randomUUID());
    }
}
