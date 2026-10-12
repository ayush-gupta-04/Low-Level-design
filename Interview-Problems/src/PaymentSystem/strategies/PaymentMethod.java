package PaymentSystem.strategies;

import PaymentSystem.entities.Payment;
import PaymentSystem.entities.PaymentExecutionResult;

public interface PaymentMethod {

    // A real adapter should forward the idempotency key to
    // the external payment provider as well.
    PaymentExecutionResult process(Payment payment, String idempotencyKey);
}