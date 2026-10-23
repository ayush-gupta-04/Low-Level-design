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
            if (payment != null && payment.getStatus() == PaymentStatus.FAILED) {
                unholdSeats(flight, seats, user.getId());
                return Optional.empty();
            }

            // If payment is PENDING or null or UNKNOWN: 
            //  - return Optional.empty();
            //  - Let the temp seat hold expire normally.
            //  - Reconcilation Process will it resolve it later in background.

        } catch (Exception e) {
            // Reason of failure can be anything.
            // We must check the payment status, and act accordingly.
            // There can be three cases .. SUCCESS, FAILURE, (PENDING or null or UNKNOWN)
            return Optional.empty();
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


// Timeout in Pay()
//    - The payment request should have a timeout so your application doesn't wait indefinitely.
//    - If Request times out, Treat the payment outcome as unknown and reconcile it with the gateway.



// 2 Edge cases : 

// Situation 1: Payment takes too long
//      Suppose User A books seats A1 and A2.
//      Step 1: 
//         - Temporarily hold the seats
//         - User A locks A1 and A2. 
//         - Start a timer, say 5 minutes.
//      Step 2: 
//         - Payment gets stuck
//         - The payment request is taking longer than 5 minutes. 
//         - User A's seats remain temporarily held until the hold expires.
//      Step 3: 
//         - The timer expires
//         - The timer checks that the same booking attempt still owns the hold and that the seats are still LOCKED. 
//         - It releases them to AVAILABLE.
//      Step 4: 
//         - User B books the seats
//         - User B successfully holds and books A1 and A2.
//      Step 5: 
//         - User A's payment finally succeeds
//         - confirmSeats() fails because User A no longer owns the seat hold. 
//         - Do not create User A's booking; initiate a refund instead.

//      Run the payment with a timeout so your thread isn’t stuck.
//      But it only interrupts your thread.
//      The gateway may still be processing, so a timeout is the same unknown outcome



// Situation 2 : 
//     Suppose the payment gateway successfully charges User A, but a network error occurs before your application receives the response. 
//     Your code enters the catch block, even though the money has been deducted.
//     - Payment throws an exception
//     - Payment outcome is UNKNOWN, not necessarily FAILED.

//     - Check the payment status : 
//       Query the payment gateway using the payment attempt ID, or wait for its callback.

//     Case 1 : Payment failed
//              - unholdSeats() immediately if it is still locked by the same user.
//     Case 2 : Still unknown?
//              Allow the seat hold to expire normally.
//              If payment is later confirmed successful after the hold is lost, initiate a refund.
//     Case 3 : Payment succeeded 
//              Confirm the seats if the hold is still valid and owned by the same User.
//              Otherwise, initiate a refund.


// The reconciliation
//     A background worker periodically checks the gateway, or processes a payment webhook, until the actual payment outcome is known.