package FlightBooking.strategies.payment;

import java.util.UUID;

import FlightBooking.entities.Payment;
import FlightBooking.enums.PaymentMethod;
import FlightBooking.enums.PaymentStatus;
import FlightBooking.strategies.PaymentStrategy;

/** UPI payment. (Simulated: validates the UPI id format only.) */
public class UpiPaymentStrategy implements PaymentStrategy {
    @Override
    public Payment pay(double amount) {
        Boolean success = Math.random() > 0.5;
        if(success){
            return new Payment(amount, PaymentMethod.UPI, PaymentStatus.SUCCESS, "UPI_" + UUID.randomUUID());
        }
        return new Payment(amount, PaymentMethod.UPI, PaymentStatus.FAILED, "UPI_" + UUID.randomUUID());
    }
}
