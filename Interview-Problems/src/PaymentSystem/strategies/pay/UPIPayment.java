package PaymentSystem.strategies.pay;

import PaymentSystem.entities.Payment;
import PaymentSystem.entities.PaymentExecutionResult;
import PaymentSystem.strategies.PaymentMethod;

public final class UPIPayment implements PaymentMethod {

    @Override
    public PaymentExecutionResult process(Payment payment, String idempotencyKey) {
        System.out.println("Processing UPI payment: " + payment.getPaymentId());
        // Mock provider response for demonstration.
        return PaymentExecutionResult.success();
    }
}