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
import FlightBooking.enums.SeatStatus;
import FlightBooking.strategies.PaymentStrategy;

// TODO : 
// 1. Add Booking the same FlightSeats for Connecting Flights.



public class BookingManager {
    private static volatile BookingManager instance;
    private final Map<String, Booking> bookings;

    private BookingManager() {
        this.bookings = new ConcurrentHashMap<>();
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

    public Optional<Booking> createBooking(Passenger user, Flight flight,Map<FlightSeat, Passenger> assignment, PaymentStrategy paymentStrategy) {

        List<FlightSeat> seats = new ArrayList<>(assignment.keySet());

        // 1. Temp Hold Seats before taking payment.
        if (!holdSeats(flight, seats, user.getId())) {
            return Optional.empty();
        }

        double amount = flight.getPricingStrategy().calculateFare(seats);

        try {

            Payment payment = paymentStrategy.pay(amount);

            // If payment surely fails .. unholdSeats() immediately.
            if (payment == null || payment.getStatus() != PaymentStatus.SUCCESS) {
                unholdSeats(flight, seats, user.getId());
                return Optional.empty();
            }

        } catch (Exception e) {
            // Reason of failure can be anything.
            // We must check then mark the status to CANCELLED.
            unholdSeats(flight, seats, user.getId());
        }



        String bookingId = UUID.randomUUID().toString();

        // Atomically confirm the reservation.
        if (!confirmSeats(flight, assignment, user.getId(), bookingId)) {
            // If I cannot finalize .. because timer restored the temporary hold.
            // If payment succeeded, initiate an idempotent refund through a refund/payment service here.
            return Optional.empty();
        }

        System.out.println("Booking Created!");
        Booking booking = new Booking(bookingId, user, flight, assignment, amount);
        bookings.put(booking.getId(), booking);
        return Optional.of(booking);
    }

    // cancel the whole booking
    public boolean cancelBooking(String userId, String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null || !userId.equals(booking.getUser().getId())) {
            return false;
        }
        Flight flight = booking.getFlight();

        synchronized (flight) {
            if (booking.getStatus() != BookingStatus.CONFIRMED) return false;
            if (!releaseBookedSeats(flight, booking.getFlightSeats(), booking.getId())) return false;
            booking.setStatus(BookingStatus.CANCELLED);
            return true;
        }
    }

    // Cancels only the given passengers from a booking; the rest stay confirmed.
    // If every passenger is cancelled, the booking itself becomes CANCELLED.
    public boolean cancelPassengers(String userId, String bookingId, Set<String> passengerIds) {
        Booking booking = bookings.get(bookingId);

        if (booking == null || !userId.equals(booking.getUser().getId())) return false;
        
        Flight flight = booking.getFlight();
        synchronized (flight) {
            if (booking.getStatus() != BookingStatus.CONFIRMED) return false;

            // Find the seats of the requested passengers.
            List<FlightSeat> seatsToCancel = new ArrayList<>();
            for (Map.Entry<FlightSeat, Passenger> e : booking.getSeatAssignments().entrySet()) {
                String passengerId = e.getValue().getId();
                if (passengerIds.contains(passengerId)) {
                    seatsToCancel.add(e.getKey());
                }
            }

            if (!releaseBookedSeats(flight, seatsToCancel, bookingId)) return false;
            booking.removeSeats(seatsToCancel);
            return true;
        }
    }




    // ------------------ HELPER functions ------------------

    // Atomically Hold Seats (check + hold)
    // TODO : Ideally a timer should unlock the the Seats if it is still Locked.

    private boolean holdSeats(Flight flight, List<FlightSeat> seats, String userId) {
        List<FlightSeat> requestedSeats = List.copyOf(seats);
        synchronized (flight) {
            for (FlightSeat seat : requestedSeats) {
                if (!flight.ownsFlightSeat(seat)) {
                    return false;
                }
                if (seat.getStatus() != SeatStatus.AVAILABLE) {
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
    private void unholdSeats(Flight flight, List<FlightSeat> seats, String userId) {
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
    private boolean confirmSeats(Flight flight, Map<FlightSeat, Passenger> assignment, String userId, String bookingId) {
        synchronized (flight) {
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
    private boolean releaseBookedSeats(Flight flight, List<FlightSeat> seats, String bookingId) {
        synchronized (flight) {
            for (FlightSeat seat : seats) {
                if (!flight.ownsFlightSeat(seat) || seat.getStatus() != SeatStatus.BOOKED || !bookingId.equals(seat.getBookingId())) {
                    return false;
                }
            }
            for (FlightSeat seat : seats) {
                seat.setPassenger(null);
                seat.setBookingId(null);
                seat.setStatus(SeatStatus.AVAILABLE);
            }
            return true;
        }
    }
}