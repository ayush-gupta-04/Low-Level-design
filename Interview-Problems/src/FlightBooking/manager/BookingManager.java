package FlightBooking.manager;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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

/** Booking flow: lock seats -> pay -> confirm seats -> create booking. */
public class BookingManager {
    private final SeatManager seatManager;
    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();

    public BookingManager(SeatManager seatManager) {
        this.seatManager = seatManager;
    }

    public Optional<Booking> createBooking(Flight flight, List<Passenger> passengers,
                                           List<FlightSeat> seats, PaymentStrategy paymentStrategy) {
        if (flight == null || passengers == null || seats == null || paymentStrategy == null
                || passengers.isEmpty() || passengers.size() != seats.size()) {
            System.out.println("Invalid request: every passenger needs exactly one seat.");
            return Optional.empty();
        }

        // 1. Lock the seats BEFORE taking payment, so nobody else can grab them.
        if (!seatManager.lockSeats(flight, seats)) {
            System.out.println("Could not lock the requested seats.");
            return Optional.empty();
        }

        boolean confirmed = false;
        try {
            // 2. Calculate the fare and take payment.
            double fare = flight.getPricingStrategy().calculateFare(seats);
            Payment payment = paymentStrategy.pay(fare);
            if (payment == null || payment.getStatus() != PaymentStatus.SUCCESS) {
                System.out.println("Payment failed. Booking not created.");
                return Optional.empty();
            }

            // 3. Payment succeeded, so turn LOCKED seats into BOOKED seats.
            String bookingId = "BK-" + UUID.randomUUID().toString().substring(0, 8);
            if (!seatManager.confirmSeats(flight, seats, bookingId)) {
                System.out.println("Seat confirmation failed. Booking not created.");
                return Optional.empty();
            }
            confirmed = true;

            // 4. Create the CONFIRMED booking.
            Booking booking = new Booking(bookingId, flight, passengers, seats, fare, payment);
            bookings.put(bookingId, booking);
            return Optional.of(booking);

        } finally {
            // Any failure or exception above frees the seats again.
            if (!confirmed) {
                seatManager.releaseLockedSeats(flight, seats);
            }
        }
    }

    public boolean cancelBooking(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            return false;
        }

        Flight flight = booking.getFlight();
        synchronized (flight) {
            if (booking.getStatus() != BookingStatus.CONFIRMED) {
                return false;   // already cancelled
            }
            if (!seatManager.releaseBookedSeats(flight, booking.getFlightSeats(), bookingId)) {
                return false;
            }
            booking.markCancelled();
            return true;
        }
    }

    public Booking getBooking(String bookingId) {
        return bookings.get(bookingId);
    }
}
