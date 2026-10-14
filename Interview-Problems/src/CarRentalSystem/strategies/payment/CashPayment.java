package CarRentalSystem.strategies.payment;
import CarRentalSystem.strategies.PaymentMethod;

public class CashPayment implements PaymentMethod {
    public boolean pay(double amount) {
        System.out.println("Paid Rs." + amount + " in Cash");
        return true;
    }
}