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



// TODO : Edge cases handling in every method.

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
            Payment payment = paymentStrategy.pay(amount);
            
            // If payment surely fails .. unholdSeats() immediately.
            if (payment == null || payment.getStatus() != PaymentStatus.SUCCESS) {
                unholdSeats(show, seats, user.getId());
                return Optional.empty();
            }

        }catch (Exception e){
            // Reason of failure can be anything.
            // We must check then mark the status to CANCELLED.
            unholdSeats(show, seats, user.getId());
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
