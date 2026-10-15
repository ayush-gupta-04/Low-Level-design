package PaymentSystem.repository.paymentattempt;

import java.util.Optional;

import PaymentSystem.entities.PaymentAttempt;

public interface PaymentAttemptRepository {

    // Atomically insert a new attempt if the key is unused.
    // Returns the new attempt if inserted, otherwise the
    // attempt that was already stored.
    PaymentAttempt tryCreatingPaymentAttempt(PaymentAttempt attempt);
    Optional<PaymentAttempt> findByKey(String idempotencyKey);
}
