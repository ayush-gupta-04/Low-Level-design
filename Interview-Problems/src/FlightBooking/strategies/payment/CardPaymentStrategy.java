package FlightBooking.strategies.payment;

import java.util.UUID;

import FlightBooking.entities.Payment;
import FlightBooking.enums.PaymentMethod;
import FlightBooking.enums.PaymentStatus;
import FlightBooking.strategies.PaymentStrategy;

/** Credit / Debit card payment. (Simulated: validates the card details only.) */
public class CardPaymentStrategy implements PaymentStrategy {
    @Override
    public Payment pay(double amount) {
        Boolean success = Math.random() > 0.5;
        if(success){
            return new Payment(amount, PaymentMethod.CARD, PaymentStatus.SUCCESS, "CARD_" + UUID.randomUUID());
        }
        return new Payment(amount, PaymentMethod.CARD, PaymentStatus.FAILED, "CARD_" + UUID.randomUUID());
    }
}
