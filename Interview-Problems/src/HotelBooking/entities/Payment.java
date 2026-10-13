package HotelBooking.entities;

import java.util.UUID;

import HotelBooking.enums.PaymentStatus;

public class Payment {
    private final String id;
    private PaymentStatus status;
    private final double amount;
    private final String transactionId;

    public Payment(double amount, PaymentStatus status, String transactionId) {
        this.id = UUID.randomUUID().toString();
        this.amount = amount;
        this.status = status;
        this.transactionId = transactionId;
    }

    public String getId() { return id; }
    public PaymentStatus getStatus() { return status; }
    public double getAmount() { return amount; }
    public String getTransactionId() { return transactionId; }
}
