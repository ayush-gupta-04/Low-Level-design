package CarRentalSystem.strategies.payment;
import CarRentalSystem.entities.Payment;
import CarRentalSystem.enums.PaymentMethod;
import CarRentalSystem.enums.PaymentStatus;
import CarRentalSystem.strategies.PaymentStrategy;

public class CashPayment implements PaymentStrategy {
    public Payment pay(double amount) {
        System.out.println("Paying through cash : " + amount);
        return new Payment(amount, PaymentMethod.UPI, PaymentStatus.SUCCESS);
    }
}