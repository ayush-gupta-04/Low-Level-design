package FlightBooking.entities;

import java.util.UUID;

import FlightBooking.enums.PaymentMethod;
import FlightBooking.enums.PaymentStatus;

public class Payment {
    private final String id;
    private final double amount;
    private final PaymentMethod method;
    private final PaymentStatus status;
    private final String transactionId;

    public Payment(double amount, PaymentMethod method, PaymentStatus status, String transactionId) {
        this.id = UUID.randomUUID().toString();
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.transactionId = transactionId;
    }

    public String getId() { return id; }
    public double getAmount() { return amount; }
    public PaymentMethod getMethod() { return method; }
    public PaymentStatus getStatus() { return status; }
    public String getTransactionId() { return transactionId; }
}
