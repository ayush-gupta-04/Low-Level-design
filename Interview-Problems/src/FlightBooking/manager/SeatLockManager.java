package FlightBooking.manager;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import FlightBooking.entities.Flight;
import FlightBooking.entities.FlightSeat;
import FlightBooking.entities.Passenger;
import FlightBooking.enums.SeatStatus;

/*
    Lock ownership is stored on the FlightSeat itself (status + lockedBy).
    The lock belongs to the BOOKER (userId), not to the passengers.
    Passengers are attached to seats only at confirm time.

    Lock Seats    : all-or-nothing, synchronized on the flight instance.
    Confirm Seats : booker must still hold every lock; binds one passenger per seat.
    Unlock Seats  : releases only seats still LOCKED by this booker; BOOKED seats untouched.
*/
public class SeatLockManager {
    private static volatile SeatLockManager instance;

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

    /** Atomically locks every requested FlightSeat for the booker, or locks none of them. */
    public boolean lockSeats(Flight flight, List<FlightSeat> seats, String userId) {
        if (flight == null || seats == null || seats.isEmpty() || userId == null || userId.isEmpty()) {
            return false;
        }
        List<FlightSeat> requestedSeats = List.copyOf(seats);

        synchronized (flight) {
            // Validate the entire request before changing any seat's state.
            for (FlightSeat seat : requestedSeats) {
                if (!flight.ownsFlightSeat(seat)) {
                    return false;
                }
                if (seat.getStatus() != SeatStatus.AVAILABLE) {
                    System.out.println("Seat " + seat.getSeatNumber() + " is not available.");
                    return false;
                }
            }

            for (FlightSeat seat : requestedSeats) {
                seat.setStatus(SeatStatus.LOCKED);
                seat.setLockedBy(userId);
            }
            return true;
        }
    }

    /**
     * Confirms seats only if the booker still holds every lock,
     * and binds exactly one passenger to each seat.
     * A Map guarantees each seat appears only once.
     */
    public boolean confirmSeats(Flight flight, Map<FlightSeat, Passenger> assignment, String userId, String bookingId) {
        if (flight == null || assignment == null || assignment.isEmpty() || userId == null) {
            return false;
        }

        synchronized (flight) {
            // Validate everything first.
            for (Map.Entry<FlightSeat, Passenger> e : assignment.entrySet()) {
                FlightSeat seat = e.getKey();
                if (e.getValue() == null || !flight.ownsFlightSeat(seat) || !seat.isLockedBy(userId)) {
                    return false;
                }
            }

            // All locks verified; commit the whole set.
            for (Map.Entry<FlightSeat, Passenger> e : assignment.entrySet()) {
                FlightSeat seat = e.getKey();
                seat.setStatus(SeatStatus.BOOKED);
                seat.setBookingId(bookingId);
                seat.setPassenger(e.getValue());
                seat.setLockedBy(null);
            }
            return true;
        }
    }

    /** Releases only seats currently locked by userId; booked seats stay booked. */
    public void unlockSeats(Flight flight, List<FlightSeat> seats, String userId) {
        if (flight == null || seats == null || userId == null) {
            return;
        }

        synchronized (flight) {
            for (FlightSeat seat : seats) {
                if (flight.ownsFlightSeat(seat) && seat.isLockedBy(userId)) {
                    seat.setStatus(SeatStatus.AVAILABLE);
                    seat.setLockedBy(null);
                    System.out.println("Unlocked seat: " + seat.getSeatNumber() + " for flight " + flight.getId());
                }
            }
        }
    }

    // Release the seats only if they are still owned by the same bookingId.
    public boolean releaseBookedSeats(Flight flight, List<FlightSeat> seats, String bookingId) {
        if (flight == null || seats == null || seats.isEmpty() || bookingId == null || bookingId.isEmpty()) {
            return false;
        }

        synchronized (flight) {
            // Validate every seat before changing anything.
            for (FlightSeat seat : seats) {
                if (!flight.ownsFlightSeat(seat)
                        || seat.getStatus() != SeatStatus.BOOKED
                        || !bookingId.equals(seat.getBookingId())) {
                    return false;
                }
            }

            // Release all seats together after validation succeeds.
            for (FlightSeat seat : seats) {
                seat.setPassenger(null);
                seat.setBookingId(null);
                seat.setStatus(SeatStatus.AVAILABLE);
            }
            return true;
        }
    }
}