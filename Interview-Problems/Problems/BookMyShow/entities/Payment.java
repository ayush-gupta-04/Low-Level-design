package entities;

import java.util.UUID;
import enums.PaymentStatus;

public class Payment {
    private String id;
    private PaymentStatus status;
    private double amount;
    private String tnxId;

    public Payment(double amount, PaymentStatus paymentStatus, String transactionId){
        this.id = UUID.randomUUID().toString();
        this.status = paymentStatus;
        this.amount = amount;
        this.tnxId = transactionId;
    }

    public String getId() { return id; }
    public PaymentStatus getStatus() { return status; }
    public double getAmount() { return amount; }
}