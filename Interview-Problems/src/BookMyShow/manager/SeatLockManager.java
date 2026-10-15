package BookMyShow.manager;

import java.util.List;

import BookMyShow.entities.Show;
import BookMyShow.entities.ShowSeat;
import BookMyShow.enums.SeatStatus;

/*
    Lock ownership is stored on the ShowSeat itself (status + lockedBy),
    so there is no separate lock map to keep in sync.

    Lock Seats :
    - Show level locking (synchronized on the show).
    - Validate the whole request first: seats belong to the show, are unique, and AVAILABLE.
    - Mark every seat LOCKED with lockedBy = userId, then schedule an auto-unlock.

    Confirm Seats : every seat must still be LOCKED by this user.
    Unlock Seats  : only releases seats still LOCKED by this user; BOOKED seats are untouched.
*/
public class SeatLockManager {
    private static SeatLockManager instance;

    private SeatLockManager() { }

    public static SeatLockManager getInstance() {
        if (instance == null) {
            synchronized (SeatLockManager.class) {
                if (instance == null) {
                    instance = new SeatLockManager();
                }
            }
        }
        return instance;
    }

    /** Atomically locks every requested ShowSeat, or locks none of them. */
    public boolean lockSeats(Show show, List<ShowSeat> seats, String userId) {
        if (show == null || seats == null || seats.isEmpty() || userId == null || userId.isEmpty()) {
            return false;
        }
        List<ShowSeat> requestedSeats = List.copyOf(seats);

        synchronized (show) {
            // Validate the entire request before changing any seat's state.

            for (ShowSeat showSeat : requestedSeats) {
                if (!show.ownsShowSeat(showSeat)) {
                    return false;
                }
                if (showSeat.getStatus() != SeatStatus.AVAILABLE) {
                    System.out.println("Seat " + showSeat.getSeatId() + " is not available.");
                    return false;
                }
            }

            for (ShowSeat showSeat : requestedSeats) {
                showSeat.setStatus(SeatStatus.LOCKED);
                showSeat.setLockedBy(userId);
            }
            return true;
        }
    }

    /** Confirms seats only if every requested seat is still locked by this user. */
    public boolean confirmSeats(Show show, List<ShowSeat> seats, String userId, String bookingId) {
        if (show == null || seats == null || seats.isEmpty() || userId == null) {
            return false;
        }

        synchronized (show) {
            for (ShowSeat showSeat : seats) {
                if (!show.ownsShowSeat(showSeat) || !showSeat.isLockedBy(userId)) {
                    return false;
                }
            }

            // All locks verified; commit the whole set.
            for (ShowSeat showSeat : seats) {
                showSeat.setStatus(SeatStatus.BOOKED);
                showSeat.setBookingId(bookingId);
                showSeat.setLockedBy(null);
            }
            return true;
        }
    }

    /** Releases only seats currently locked by userId; booked seats stay booked. */
    public void unlockSeats(Show show, List<ShowSeat> seats, String userId) {
        if (show == null || seats == null || userId == null) {
            return;
        }

        synchronized (show) {
            for (ShowSeat showSeat : seats) {
                if (show.ownsShowSeat(showSeat) && showSeat.isLockedBy(userId)) {
                    showSeat.setStatus(SeatStatus.AVAILABLE);
                    showSeat.setLockedBy(null);
                    System.out.println("Unlocked seat: " + showSeat.getSeatId() + " for show " + show.getId());
                }
            }
        }
    }

    // Release the seats if the seats is still owned by the same bookingId.
    public boolean releaseBookingSeats(Show show, List<ShowSeat> seats, String bookingId) {
        if (show == null || seats == null || seats.isEmpty() || bookingId == null || bookingId.isEmpty()) {
            return false;
        }
        synchronized (show) {
            // Validate every seat before changing anything.
            for (ShowSeat seat : seats) {
                if (!show.ownsShowSeat(seat) || seat.getStatus() != SeatStatus.BOOKED || !bookingId.equals(seat.getBookingId())) {
                    return false;
                }
            }
            // Release all seats together after validation succeeds.
            for (ShowSeat seat : seats) {
                seat.setBookingId(null);
                seat.setStatus(SeatStatus.AVAILABLE);
            }
            return true;
        }
    }
}