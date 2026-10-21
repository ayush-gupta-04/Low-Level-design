package CarRentalSystem.strategies.payment;
import CarRentalSystem.entities.Payment;
import CarRentalSystem.enums.PaymentMethod;
import CarRentalSystem.enums.PaymentStatus;
import CarRentalSystem.strategies.PaymentStrategy;

public class CashPayment implements PaymentStrategy {
    public Payment pay(String billId, double amount) {
        return new Payment(billId, amount, PaymentMethod.UPI, PaymentStatus.SUCCESS);
    }
}