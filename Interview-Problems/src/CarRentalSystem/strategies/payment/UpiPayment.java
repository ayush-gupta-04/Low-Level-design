package CarRentalSystem.strategies.payment;

import CarRentalSystem.strategies.PaymentMethod;

public class UpiPayment implements PaymentMethod {
    public boolean pay(double amount) {
        System.out.println("Paid Rs." + amount + " via UPI");
        return true;
    }
}
