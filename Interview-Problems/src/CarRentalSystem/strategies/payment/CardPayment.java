package CarRentalSystem.strategies.payment;

import CarRentalSystem.strategies.PaymentMethod;

public class CardPayment implements PaymentMethod {
    public boolean pay(double amount) {
        System.out.println("Paid Rs." + amount + " via Card");
        return true;
    }
}
