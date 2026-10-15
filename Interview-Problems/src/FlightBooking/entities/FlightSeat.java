package FlightBooking.entities;

import FlightBooking.enums.SeatStatus;

/**
 * A physical seat's booking state for one particular flight instance (flight + date).
 *
 * Ownership lives on the seat itself:
 *  - lockedBy  : userId (the booker) holding the temporary lock; null unless LOCKED
 *  - bookingId : booking (PNR) that owns the seat; null unless BOOKED
 *  - passenger : who will sit here; set on confirm, null unless BOOKED
 */
public class FlightSeat {
    private final String flightId;
    private final Seat seat;
    private SeatStatus status;
    private String lockedBy;
    private String bookingId;
    private Passenger passenger;

    public FlightSeat(String flightId, Seat seat) {
        this.flightId = flightId;
        this.seat = seat;
        this.status = SeatStatus.AVAILABLE;
    }

    public String getFlightId() { return flightId;}
    public Seat getSeat() { return seat; }
    public String getSeatNumber() { return seat.getSeatNumber();}
    public SeatStatus getStatus() { return status; }
    public String getLockedBy() { return lockedBy; }
    public String getBookingId() { return bookingId; }
    public Passenger getPassenger() { return passenger; }

    /** True only if the seat is currently LOCKED by this exact user (the booker). */
    public boolean isLockedBy(String userId) {
        return status == SeatStatus.LOCKED && userId != null && userId.equals(lockedBy);
    }

    /**
     * State changes should only be performed by SeatLockManager / BookingManager
     * while synchronized on the owning FlightInstance.
     */
    public void setStatus(SeatStatus status) { this.status = status; }
    public void setLockedBy(String lockedBy) { this.lockedBy = lockedBy; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public void setPassenger(Passenger passenger) { this.passenger = passenger; }
}