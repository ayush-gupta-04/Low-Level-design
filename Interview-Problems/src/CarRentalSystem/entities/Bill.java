package CarRentalSystem.entities;

import java.util.UUID;

/** Data object representing the charges for a reservation. */
public class Bill {
    private final String billId;
    private final Reservation reservation;
    private final double rentalAmount;
    private final double lateFee;

    public Bill(Reservation reservation, double rentalAmount, double lateFee) {
        this.billId = "BILL_" + UUID.randomUUID().toString();
        this.reservation = reservation;
        this.rentalAmount = rentalAmount;
        this.lateFee = lateFee;
    }

    public String getBillId(){
        return this.billId;
    }
    public Reservation getReservation() {
        return reservation;
    }

    public double getRentalAmount() {
        return rentalAmount;
    }

    public double getLateFee() {
        return lateFee;
    }

    public double getTotal() {
        return rentalAmount + lateFee;
    }
}
