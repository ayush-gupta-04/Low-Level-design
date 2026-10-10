package strategies;

import java.util.UUID;

import entities.Payment;
import enums.PaymentStatus;

public class CreditCardPaymentStrategy implements PaymentStrategy{
    public Payment pay(double amount){
        Boolean success = Math.random() > 0.5;
        if(success){
            return new Payment(amount, PaymentStatus.SUCCESS, "TNX_" + UUID.randomUUID().toString());
        }
        return new Payment(amount, PaymentStatus.FAILED, "TNX_" + UUID.randomUUID().toString());
    }
}
