package BookMyShow.entities;

import BookMyShow.enums.SeatStatus;

/**
 * A physical seat's booking state for one particular show.
 * Each Show creates its own ShowSeat instance for every Seat on its Screen.
 *
 * Ownership lives on the seat itself:
 *  - lockedBy  : userId holding the temporary lock (set while LOCKED, null otherwise)
 *  - bookingId : booking that owns the seat (set while BOOKED, null otherwise)
 */
public class ShowSeat {
    private final String showId;
    private final Seat seat;
    private SeatStatus status;
    private String lockedBy;
    private String bookingId;

    public ShowSeat(String showId, Seat seat) {
        this.showId = showId;
        this.seat = seat;
        this.status = SeatStatus.AVAILABLE;
    }

    public String getShowId() { return showId; }
    public Seat getSeat() { return seat; }
    public String getSeatId() { return seat.getId(); }
    public SeatStatus getStatus() { return status; }
    public String getLockedBy() { return lockedBy; }
    public String getBookingId() { return bookingId; }

    /** True only if the seat is currently LOCKED by this exact user. */
    public boolean isLockedBy(String userId) {
        return status == SeatStatus.LOCKED && userId != null && userId.equals(lockedBy);
    }

    /**
     * State changes should only be performed by SeatLockManager / BookingManager
     * while synchronized on the owning Show.
     */
    public void setStatus(SeatStatus status) { this.status = status; }
    public void setLockedBy(String lockedBy) { this.lockedBy = lockedBy; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
}