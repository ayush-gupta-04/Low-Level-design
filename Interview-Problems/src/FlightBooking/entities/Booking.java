package FlightBooking.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import FlightBooking.enums.BookingStatus;

/**
 * A confirmed booking (PNR): one booker, one flight instance, one passenger per seat.
 * seatAssignments holds only the ACTIVE seats; it shrinks as passengers are cancelled.
 */
public class Booking {
    private final String id;
    private final Passenger user;                                  // the booker
    private final Flight flight;
    private final Map<FlightSeat, Passenger> seatAssignments; // active seat -> passenger
    private final double amount;
    private final Payment payment;
    private BookingStatus status;

    public Booking(String id, Passenger user, Flight flight, Map<FlightSeat, Passenger> seatAssignments, double amount, Payment payment) {
        this.id = id;
        this.user = user;
        this.flight = flight;
        // Defensive copy, order preserved, so later caller-side changes can't affect the booking.
        this.seatAssignments = new LinkedHashMap<>(seatAssignments);
        this.amount = amount;
        this.payment = payment;
        this.status = BookingStatus.CONFIRMED;
    }

    public String getId() { return id; }
    public Passenger getUser() { return user; }
    public Flight getFlight() { return flight; }
    public Map<FlightSeat, Passenger> getSeatAssignments() { return new LinkedHashMap<>(seatAssignments); }
    public List<FlightSeat> getFlightSeats() { return new ArrayList<>(seatAssignments.keySet()); }
    public List<Passenger> getPassengers() { return new ArrayList<>(seatAssignments.values()); }
    public double getAmount() { return amount; }
    public Payment getPayment() { return payment; }
    public BookingStatus getStatus() { return status; }
    public void markCancelled() { this.status = BookingStatus.CANCELLED; }

    /**
     * Drops cancelled seats from the booking; cancels the whole booking if none are left.
     * Call only while holding the flight's lock, after the seats have been released.
     */
    public void removeSeats(Collection<FlightSeat> cancelledSeats) {
        seatAssignments.keySet().removeAll(cancelledSeats);
        if (seatAssignments.isEmpty()) {
            markCancelled();
        }
    }
}