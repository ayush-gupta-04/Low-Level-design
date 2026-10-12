package PaymentSystem.entities;

import PaymentSystem.enums.PaymentStatus;

public final class PaymentResult {
    private final String paymentId;
    private final PaymentStatus status;
    private final String message;

    private PaymentResult(
            String paymentId,
            PaymentStatus status,
            String message) {

        this.paymentId = paymentId;
        this.status = status;
        this.message = message;
    }

    public static PaymentResult success(String paymentId) {
        return new PaymentResult(
                paymentId,
                PaymentStatus.SUCCESS,
                "Payment successful");
    }

    public static PaymentResult failed(
            String paymentId, String reason) {

        return new PaymentResult(
                paymentId,
                PaymentStatus.FAILED,
                reason);
    }

    public static PaymentResult inProgress(String paymentId) {
        return new PaymentResult(
                paymentId,
                PaymentStatus.IN_PROGRESS,
                "Payment is still processing. Check again later.");
    }

    public String getPaymentId() {
        return paymentId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "PaymentResult{" +
                "paymentId='" + paymentId + '\'' +
                ", status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}