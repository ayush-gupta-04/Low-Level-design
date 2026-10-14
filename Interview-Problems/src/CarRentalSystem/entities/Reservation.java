package CarRentalSystem.entities;

import java.time.LocalDate;

import CarRentalSystem.enums.ReservationStatus;

public class Reservation {
    private final String id;
    private final Customer customer;
    private final Vehicle vehicle;
    private final LocalDate startDate;
    private final LocalDate endDate;      // planned return date
    private ReservationStatus status = ReservationStatus.CONFIRMED;
 
    public Reservation(String id, Customer customer, Vehicle vehicle, LocalDate start, LocalDate end) {
        this.id = id;
        this.customer = customer;
        this.vehicle = vehicle;
        this.startDate = start;
        this.endDate = end;
    }
 
    public String getId() { return id; }
    public Customer getCustomer() { return customer; }
    public Vehicle getVehicle() { return vehicle; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
}
