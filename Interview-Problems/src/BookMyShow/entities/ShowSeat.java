package BookMyShow.entities;

import BookMyShow.enums.SeatStatus;

/**
 * A physical seat's booking state for one particular show.
 * Each Show creates its own ShowSeat instance for every Seat on its Screen.
 */


/*
    During confirmation, the SeatLockManager will save the booking ID on every booked ShowSeat.
    During cancellation, it will clear that ID and restore AVAILABLE.
    This ownership check is important: you must not free a seat just because a booking happens to reference it. 
    You must verify that the seat is still booked by that exact booking.
*/


public class ShowSeat {
    private final String showId;
    private final Seat seat;
    private SeatStatus status;
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
    public String getBookingId() { return bookingId; }

    /**
     * State changes should normally be performed by SeatLockManager while
     * synchronized on the owning Show.
     */
    public void setStatus(SeatStatus status) {
        this.status = status;
    }
    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }
}
