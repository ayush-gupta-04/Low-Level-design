package PaymentSystem.entities;

public final class PaymentExecutionResult {
    private final boolean successful;
    private final String message;

    private PaymentExecutionResult(
            boolean successful, String message) {

        this.successful = successful;
        this.message = message;
    }

    public static PaymentExecutionResult success() {
        return new PaymentExecutionResult(
                true, "Payment approved by provider");
    }

    public static PaymentExecutionResult failure(String reason) {
        return new PaymentExecutionResult(false, reason);
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getMessage() {
        return message;
    }
}
