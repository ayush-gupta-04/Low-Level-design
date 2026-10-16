package CarRentalSystem.services;

import CarRentalSystem.strategies.PaymentMethod;

/** Executes a payment through the selected payment strategy. */
public class PaymentService {
    public boolean processPayment(PaymentMethod paymentMethod, double amount) {
        if (paymentMethod == null) {
            return false;
        }
        return paymentMethod.pay(amount);
    }
}
