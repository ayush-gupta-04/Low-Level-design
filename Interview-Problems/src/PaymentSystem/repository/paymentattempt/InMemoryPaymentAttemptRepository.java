package PaymentSystem.repository.paymentattempt;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import PaymentSystem.entities.PaymentAttempt;

public final class InMemoryPaymentAttemptRepository implements PaymentAttemptRepository {

    private final Map<String, PaymentAttempt> attempts = new ConcurrentHashMap<>();  // IdempotencyKey -> PaymentAttempt

    // It is synchronised because i am doing only 1 operation.
    // don't need to synchronised here.
    @Override
    public PaymentAttempt tryCreatingPaymentAttempt(PaymentAttempt attempt) {
        PaymentAttempt existing = attempts.putIfAbsent(attempt.getIdempotencyKey(), attempt);
        return existing == null ? attempt : existing;
    }

    @Override
    public Optional<PaymentAttempt> findByKey(String idempotencyKey) {
        PaymentAttempt attempt =  attempts.get(idempotencyKey);
        return attempt == null ? Optional.empty() : Optional.of(attempt);
    }
}