package BookMyShow.manager;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import BookMyShow.entities.Booking;
import BookMyShow.entities.Payment;
import BookMyShow.entities.Show;
import BookMyShow.entities.ShowSeat;
import BookMyShow.entities.User;
import BookMyShow.enums.BookingStatus;
import BookMyShow.enums.PaymentStatus;
import BookMyShow.enums.SeatStatus;
import BookMyShow.strategies.PaymentStrategy;


public class BookingManager {
    private static BookingManager instance;
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

    // --------------- Main Methods -----------------
    public Optional<Booking> createBooking(User user, Show show, List<ShowSeat> seats, PaymentStrategy paymentStrategy) {

        // 1. Temp Hold Seats before taking payment.
        if (!holdSeats(show, seats, user.getId())) {
            System.out.println("Could not lock the requested seats.");
            return Optional.empty();
        }

        double amount = show.getPricingStrategy().calcPrice(seats);

        try {
            Payment payment = paymentStrategy.pay(amount);  // we generally have a Timeout here .. example 1 min.
            
            // If payment surely fails .. unholdSeats() immediately if it is still locked by the same user.
            if (payment != null && payment.getStatus() == PaymentStatus.FAILED) {
                unholdSeats(show, seats, user.getId());
                return Optional.empty();
            }

            // If payment is PENDING or null or UNKNOWN: 
            //  - return Optional.empty();
            //  - Let the temp seat hold expire normally.
            //  - Reconcilation Process will it resolve it later in background.

        }catch (Exception e){
            // Reason of failure can be anything.
            // We must check the payment status, and act accordingly.
            // There can be three cases .. SUCCESS, FAILURE, (PENDING or null or UNKNOWN)
            return Optional.empty();
        }



        String bookingId = UUID.randomUUID().toString();

        // Atomically confirm the reservation.
        if (!confirmSeats(show, seats, user.getId(),bookingId)) {
            // If I cannot finalize .. because timer restored the temporary hold.
            // If payment succeeded, initiate an idempotent refund through a refund/payment service here.
            return Optional.empty();
        }

        System.out.println("Booking Created!");
        Booking booking = new Booking(bookingId, user, show, seats, amount);
        bookings.put(booking.getId(), booking);
        return Optional.of(booking);
    }

    public boolean cancelBooking(String userId, String bookingId) {
        Booking booking = bookings.get(bookingId);

        if (booking == null || !userId.equals(booking.getUser().getId())) {
            return false;
        }

        Show show = booking.getShow();

        synchronized (show) {
            if (booking.getStatus() != BookingStatus.CONFIRMED) return false;
            if (!releaseBookingSeats(show,booking.getShowSeats(),booking.getId())) return false;
            booking.setStatus(BookingStatus.CANCELLED);
            return true;
        }
    }
    // -------------- End of Main Methods -----------------





    // --------------- HELPER functions ---------------------
    // Atomically Hold Seats (check + hold)
    // TODO : Ideally a timer should unlock the the Seats if it is still Locked.

    private boolean holdSeats(Show show, List<ShowSeat> seats, String userId) {
        List<ShowSeat> requestedSeats = List.copyOf(seats);
        synchronized (show) {
            for (ShowSeat showSeat : requestedSeats) {
                if (!show.ownsShowSeat(showSeat)) return false;

                if (showSeat.getStatus() != SeatStatus.AVAILABLE) {
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
    private void unholdSeats(Show show, List<ShowSeat> seats, String userId) {
        synchronized (show) {
            for (ShowSeat showSeat : seats) {
                if (show.ownsShowSeat(showSeat) && showSeat.isLockedBy(userId)) {
                    showSeat.setStatus(SeatStatus.AVAILABLE);
                    showSeat.setLockedBy(null);
                }
            }
        }
    }
    private boolean confirmSeats(Show show, List<ShowSeat> seats, String userId, String bookingId) {
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
    // Release the seats if the seats is still owned by the same bookingId.
    private boolean releaseBookingSeats(Show show, List<ShowSeat> seats, String bookingId) {
        synchronized (show) {
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