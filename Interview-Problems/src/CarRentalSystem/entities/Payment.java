package CarRentalSystem.entities;

import java.util.UUID;

import CarRentalSystem.enums.PaymentMethod;
import CarRentalSystem.enums.PaymentStatus;

public class Payment {
    private String paymentId;
    private double amount;
    private PaymentMethod method;
    private PaymentStatus status;

    public Payment(double amount, PaymentMethod method, PaymentStatus status) {
        this.paymentId = UUID.randomUUID().toString();
        this.amount = amount;
        this.method = method;
        this.status = status;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}
