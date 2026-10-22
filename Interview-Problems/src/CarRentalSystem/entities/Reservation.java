package CarRentalSystem.entities;

import java.time.LocalDate;
import java.util.UUID;

import CarRentalSystem.enums.ReservationStatus;

public class Reservation {
    private final String id;
    private final Customer customer;
    private final Vehicle vehicle;
    private final LocalDate startDate;
    private final LocalDate endDate;      // planned return date
    private ReservationStatus status;
    private double prepaidRentalAmount;
 
    public Reservation(Customer customer, Vehicle vehicle, LocalDate start, LocalDate end) {
        this.id = "REV_" + UUID.randomUUID().toString();
        this.customer = customer;
        this.vehicle = vehicle;
        this.startDate = start;
        this.endDate = end;
        this.prepaidRentalAmount = 0.0;
        this.status = ReservationStatus.PENDING_PAYMENT;
    }
 
    public String getId() { return id; }
    public Customer getCustomer() { return customer; }
    public Vehicle getVehicle() { return vehicle; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public ReservationStatus getStatus() { return status; }
    public double getPrepaidRentalAmount(){return this.prepaidRentalAmount;};
    public void setStatus(ReservationStatus status) { this.status = status; }
    public void setPrepaidRentalAmount(double prepaidRentalAmount) {this.prepaidRentalAmount = prepaidRentalAmount;}
}
