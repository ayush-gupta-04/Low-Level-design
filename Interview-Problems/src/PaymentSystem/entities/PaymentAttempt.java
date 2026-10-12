package PaymentSystem.entities;

import java.math.BigDecimal;
import java.time.Instant;

import PaymentSystem.enums.PaymentAttemptStatus;
import PaymentSystem.enums.PaymentMethodType;

public final class PaymentAttempt {
    private final String idempotencyKey;
    private final String userId;
    private final BigDecimal amount;
    private final PaymentMethodType paymentMethod;
    private final String paymentId;
    private final Instant createdAt;

    private PaymentAttemptStatus status = PaymentAttemptStatus.IN_PROGRESS;

    private PaymentResult result;

    public PaymentAttempt(
            String idempotencyKey,
            String userId,
            BigDecimal amount,
            PaymentMethodType paymentMethod,
            String paymentId) {

        this.idempotencyKey = idempotencyKey;
        this.userId = userId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentId = paymentId;
        this.createdAt = Instant.now();
    }

    // A key cannot be reused for a different logical request.
    public boolean matchesRequest(
            String userId,
            BigDecimal amount,
            PaymentMethodType paymentMethod) {

        return this.userId.equals(userId) && this.amount.compareTo(amount) == 0 && this.paymentMethod == paymentMethod;
    }

    public synchronized void complete( PaymentResult result, PaymentAttemptStatus terminalStatus) {
        if (status != PaymentAttemptStatus.IN_PROGRESS) {
            throw new IllegalStateException("Payment attempt has already finished");
        }
        if (terminalStatus == PaymentAttemptStatus.IN_PROGRESS) {
            throw new IllegalArgumentException( "Expected a terminal attempt status");
        }
        // Publish the result before publishing the terminal status.
        this.result = result;
        this.status = terminalStatus;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public PaymentAttemptStatus getStatus() {
        return status;
    }

    public PaymentResult getResult() {
        return result;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}