package PaymentSystem.entities;

import java.math.BigDecimal;
import java.time.Instant;

import PaymentSystem.enums.*;

public final class Payment {
    private final String paymentId;
    private final String userId;
    private final BigDecimal amount;
    private final PaymentMethodType paymentMethod;
    private final Instant createdAt;
    private PaymentStatus status = PaymentStatus.PENDING;
    private Instant updatedAt = Instant.now();
    private String failureReason;

    public Payment(String paymentId, String userId, BigDecimal amount, PaymentMethodType paymentMethod) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.createdAt = Instant.now();
    }

    public synchronized void markInProgress() {
        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment cannot transition to IN_PROGRESS");
        }

        status = PaymentStatus.IN_PROGRESS;
        updatedAt = Instant.now();
    }

    public synchronized void markSuccess() {
        if (status != PaymentStatus.IN_PROGRESS) {
            throw new IllegalStateException( "Payment must be IN_PROGRESS before SUCCESS");
        }

        status = PaymentStatus.SUCCESS;
        updatedAt = Instant.now();
    }

    public synchronized void markFailed(String reason) {
        if (status != PaymentStatus.IN_PROGRESS) {
            throw new IllegalStateException("Payment must be IN_PROGRESS before FAILED");
        }

        status = PaymentStatus.FAILED;
        failureReason = reason;
        updatedAt = Instant.now();
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentMethodType getPaymentMethod() {
        return paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getFailureReason() {
        return failureReason;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", userId='" + userId + '\'' +
                ", amount=" + amount +
                ", method=" + paymentMethod +
                ", status=" + status +
                ", createdAt=" + createdAt +
                (failureReason == null
                        ? ""
                        : ", failureReason='" + failureReason + '\'') +
                '}';
    }
}
