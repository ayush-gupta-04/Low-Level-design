package FlightBooking.entities;

import java.util.List;

import FlightBooking.enums.BookingStatus;

public class Booking {
    private final String id;
    private final Flight flight;
    private final List<Passenger> passengers;
    private final List<FlightSeat> flightSeats;   // flightSeats.get(i) belongs to passengers.get(i)
    private final double totalFare;
    private final Payment payment;
    private BookingStatus status;

    public Booking(String id, Flight flight, List<Passenger> passengers,
                   List<FlightSeat> flightSeats, double totalFare, Payment payment) {
        this.id = id;
        this.flight = flight;
        this.passengers = List.copyOf(passengers);
        this.flightSeats = List.copyOf(flightSeats);
        this.totalFare = totalFare;
        this.payment = payment;
        this.status = BookingStatus.CONFIRMED;   // a Booking exists only after successful payment
    }

    public String getId() { return id; }
    public Flight getFlight() { return flight; }
    public List<Passenger> getPassengers() { return passengers; }
    public List<FlightSeat> getFlightSeats() { return flightSeats; }
    public double getTotalFare() { return totalFare; }
    public Payment getPayment() { return payment; }
    public BookingStatus getStatus() { return status; }
    public void markCancelled() { this.status = BookingStatus.CANCELLED; }
}
