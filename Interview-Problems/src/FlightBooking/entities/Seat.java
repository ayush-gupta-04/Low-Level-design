package FlightBooking.entities;

import FlightBooking.enums.SeatClass;
import FlightBooking.enums.SeatPosition;

/**
 * A physical seat on an aircraft.
 * Availability is NOT stored here, because the same physical seat can be
 * free on one flight and booked on another. See FlightSeat.
 */
public class Seat {
    private final String seatNumber;   // e.g. "5A"
    private final SeatClass seatClass;
    private final SeatPosition position;

    public Seat(String seatNumber, SeatClass seatClass, SeatPosition position) {
        this.seatNumber = seatNumber;
        this.seatClass = seatClass;
        this.position = position;
    }

    public String getSeatNumber() { return seatNumber; }
    public SeatClass getSeatClass() { return seatClass; }
    public SeatPosition getPosition() { return position; }
}
