package entities;

import enums.SeatStatus;

/**
 * A physical seat's booking state for one particular show.
 * Each Show creates its own ShowSeat instance for every Seat on its Screen.
 */


public class ShowSeat {
    private final String showId;
    private final Seat seat;
    private volatile SeatStatus status;

    public ShowSeat(String showId, Seat seat) {
        this.showId = showId;
        this.seat = seat;
        this.status = SeatStatus.AVAILABLE;
    }

    public String getShowId() { return showId; }
    public Seat getSeat() { return seat; }
    public String getSeatId() { return seat.getId(); }
    public SeatStatus getStatus() { return status; }

    /**
     * State changes should normally be performed by SeatLockManager while
     * synchronized on the owning Show.
     */
    public void setStatus(SeatStatus status) {
        this.status = status;
    }
}
