package FlightBooking.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import FlightBooking.entities.Booking;
import FlightBooking.entities.Flight;
import FlightBooking.entities.FlightSeat;
import FlightBooking.entities.Passenger;
import FlightBooking.entities.Payment;
import FlightBooking.enums.BookingStatus;
import FlightBooking.enums.PaymentStatus;
import FlightBooking.strategies.PaymentStrategy;

public class BookingManager {
    private static volatile BookingManager instance;
    private final SeatLockManager seatLockMgr;
    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();

    private BookingManager() {
        this.seatLockMgr = SeatLockManager.getInstance();
    }

    public static BookingManager getInstance() {
        if (instance == null) {
            synchronized (BookingManager.class) {
                if (instance == null) {
                    instance = new BookingManager();
                }
            }
        }
        return instance;
    }

    /**
     * The user (booker) books several seats, each assigned to one passenger.
     * The lock is held by the booker; passengers are bound to seats on confirm.
     */
    public Optional<Booking> createBooking(Passenger user, Flight flight,Map<FlightSeat, Passenger> assignment, PaymentStrategy paymentStrategy) {

        if (user == null || flight == null || assignment == null || assignment.isEmpty() || paymentStrategy == null) {
            return Optional.empty();
        }

        List<FlightSeat> seats = new ArrayList<>(assignment.keySet());

        // 1. Reserve inventory before taking payment.
        if (!seatLockMgr.lockSeats(flight, seats, user.getId())) {
            System.out.println("Could not lock the requested seats.");
            return Optional.empty();
        }

        try {
            // 2. Calculate the price and process payment.
            double amount = flight.getPricingStrategy().calculateFare(seats);
            Payment payment = paymentStrategy.pay(amount);

            if (payment == null || payment.getStatus() != PaymentStatus.SUCCESS) {
                System.out.println("Payment failed; releasing seat locks.");
                return Optional.empty();
            }

            String bookingId = UUID.randomUUID().toString();

            // 3. Confirm only if the booker still owns every lock (no timeout/race).
            if (!seatLockMgr.confirmSeats(flight, assignment, user.getId(), bookingId)) {
                System.out.println("Seat lock expired or ownership changed; booking not confirmed.");
                return Optional.empty();
            }

            Booking booking = new Booking(bookingId, user, flight, assignment, amount, payment);
            bookings.put(booking.getId(), booking);
            return Optional.of(booking);

        } finally {
            // Releases locks on payment failure/exception.
            // After confirmation the seats are BOOKED, so this is a no-op.
            seatLockMgr.unlockSeats(flight, seats, user.getId());
        }
    }

    public boolean cancelBooking(String userId, String bookingId) {
        if (userId == null || bookingId == null) {
            return false;
        }

        Booking booking = bookings.get(bookingId);

        if (booking == null || !userId.equals(booking.getUser().getId())) {
            return false;
        }

        Flight flight = booking.getFlight();

        synchronized (flight) {
            if (booking.getStatus() != BookingStatus.CONFIRMED) {
                return false;
            }

            if (!seatLockMgr.releaseBookedSeats(flight, booking.getFlightSeats(), booking.getId())) {
                return false;
            }

            booking.markCancelled();
            return true;
        }
    }

    /**
     * Cancels only the given passengers from a booking; the rest stay confirmed.
     * If every passenger is cancelled, the booking itself becomes CANCELLED.
     */
    public boolean cancelPassengers(String userId, String bookingId, Set<String> passengerIds) {
        if (userId == null || bookingId == null || passengerIds == null || passengerIds.isEmpty()) {
            return false;
        }

        Booking booking = bookings.get(bookingId);

        if (booking == null || !userId.equals(booking.getUser().getId())) {
            return false;
        }

        Flight flight = booking.getFlight();

        synchronized (flight) {
            if (booking.getStatus() != BookingStatus.CONFIRMED) {
                return false;
            }

            // Find the seats of the requested passengers.
            List<FlightSeat> seatsToCancel = new ArrayList<>();
            for (Map.Entry<FlightSeat, Passenger> e : booking.getSeatAssignments().entrySet()) {
                String passengerId = e.getValue().getId();
                if (passengerIds.contains(passengerId)) {
                    seatsToCancel.add(e.getKey());
                }
            }

            if (!seatLockMgr.releaseBookedSeats(flight, seatsToCancel, bookingId)) {
                return false;
            }

            booking.removeSeats(seatsToCancel);
            return true;
        }
    }
}