package PaymentSystem.repository.paymentattempt;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import PaymentSystem.entities.PaymentAttempt;

public final class InMemoryPaymentAttemptRepository implements PaymentAttemptRepository {

    private final Map<String, PaymentAttempt> attempts = new ConcurrentHashMap<>();

    @Override
    public PaymentAttempt createIfAbsent(PaymentAttempt attempt) {
        PaymentAttempt existing = attempts.putIfAbsent(attempt.getIdempotencyKey(), attempt);
        return existing == null ? attempt : existing;
    }

    @Override
    public Optional<PaymentAttempt> findByKey(String idempotencyKey) {
        return Optional.ofNullable(attempts.get(idempotencyKey));
    }
}