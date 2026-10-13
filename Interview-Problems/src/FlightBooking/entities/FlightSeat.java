package FlightBooking.entities;

import FlightBooking.enums.SeatStatus;

/**
 * A physical seat's booking state for ONE particular flight.
 * Every Flight creates its own FlightSeat for each Seat of its aircraft.
 */
public class FlightSeat {
    private final String flightId;
    private final Seat seat;
    private SeatStatus status;
    private String bookingId;   // set only while BOOKED

    public FlightSeat(String flightId, Seat seat) {
        this.flightId = flightId;
        this.seat = seat;
        this.status = SeatStatus.AVAILABLE;
    }

    public String getFlightId() { return flightId; }
    public Seat getSeat() { return seat; }
    public String getSeatNumber() { return seat.getSeatNumber(); }
    public SeatStatus getStatus() { return status; }
    public String getBookingId() { return bookingId; }

    // Only SeatManager should change these (while holding the flight's lock).
    public void setStatus(SeatStatus status) { this.status = status; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
}
