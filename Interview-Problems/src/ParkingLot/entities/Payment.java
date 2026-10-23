package ParkingLot.entities;

import ParkingLot.enums.PaymentMethod;
import ParkingLot.enums.PaymentStatus;

public class Payment {
    private String id;
    private PaymentMethod method;
    private PaymentStatus status;
    private double amount;

    public Payment(PaymentMethod method, PaymentStatus status, double amount) {
        this.method = method;
        this.status = status;
        this.amount = amount;
    }

    public String getId() {return id;}
    public PaymentMethod getMethod() {return method;}
    public PaymentStatus getStatus() {return status;}
    public double getAmount() {return amount;}
}
