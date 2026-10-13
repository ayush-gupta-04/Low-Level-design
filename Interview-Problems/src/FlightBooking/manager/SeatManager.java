package FlightBooking.manager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import FlightBooking.entities.Flight;
import FlightBooking.entities.FlightSeat;
import FlightBooking.enums.SeatStatus;

/**
 * Owns every seat state change: AVAILABLE -> LOCKED -> BOOKED -> AVAILABLE.
 * All methods synchronize on the Flight, so two users can never grab the same
 * seat on the same flight, while different flights do not block each other.
 */
public class SeatManager {

    /** Locks ALL the seats or NONE of them. */
    public boolean lockSeats(Flight flight, List<FlightSeat> seats) {
        if (flight == null || seats == null || seats.isEmpty()) {
            return false;
        }
        synchronized (flight) {
            // 1. Validate everything first, change nothing yet.
            Set<FlightSeat> unique = new HashSet<>();
            for (FlightSeat seat : seats) {
                if (!flight.ownsFlightSeat(seat) || !unique.add(seat)) {
                    return false;   // wrong flight, or the same seat twice
                }
                if (seat.getStatus() != SeatStatus.AVAILABLE) {
                    System.out.println("Seat " + seat.getSeatNumber() + " is not available.");
                    return false;
                }
            }
            // 2. Everything is fine, so lock them all.
            for (FlightSeat seat : seats) {
                seat.setStatus(SeatStatus.LOCKED);
            }
            return true;
        }
    }

    /** LOCKED -> BOOKED (called only after successful payment). */
    public boolean confirmSeats(Flight flight, List<FlightSeat> seats, String bookingId) {
        synchronized (flight) {
            for (FlightSeat seat : seats) {
                if (seat.getStatus() != SeatStatus.LOCKED) {
                    return false;
                }
            }
            for (FlightSeat seat : seats) {
                seat.setStatus(SeatStatus.BOOKED);
                seat.setBookingId(bookingId);
            }
            return true;
        }
    }

    /** LOCKED -> AVAILABLE (payment failed). BOOKED seats are left untouched. */
    public void releaseLockedSeats(Flight flight, List<FlightSeat> seats) {
        synchronized (flight) {
            for (FlightSeat seat : seats) {
                if (seat.getStatus() == SeatStatus.LOCKED) {
                    seat.setStatus(SeatStatus.AVAILABLE);
                }
            }
        }
    }

    /** BOOKED -> AVAILABLE (cancellation). Only if the seats still belong to this booking. */
    public boolean releaseBookedSeats(Flight flight, List<FlightSeat> seats, String bookingId) {
        synchronized (flight) {
            for (FlightSeat seat : seats) {
                if (seat.getStatus() != SeatStatus.BOOKED || !bookingId.equals(seat.getBookingId())) {
                    return false;
                }
            }
            for (FlightSeat seat : seats) {
                seat.setStatus(SeatStatus.AVAILABLE);
                seat.setBookingId(null);
            }
            return true;
        }
    }
}
