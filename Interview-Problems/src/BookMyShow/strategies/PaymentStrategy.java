package strategies;
import entities.Payment;

public interface PaymentStrategy {
    public Payment pay(double amount);
}
