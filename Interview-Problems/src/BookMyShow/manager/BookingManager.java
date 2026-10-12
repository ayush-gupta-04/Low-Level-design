package BookMyShow.manager;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

    public Optional<Booking> createBooking(User user, Show show, List<ShowSeat> seats, PaymentStrategy paymentStrategy) {

        if (user == null || show == null || seats == null || paymentStrategy == null) {
            return Optional.empty();
        }

        // 1. Reserve inventory before taking payment.
        if (!seatLockMgr.lockSeats(show, seats, user.getId())) {
            System.out.println("Could not lock the requested seats.");
            return Optional.empty();
        }

        try {
            // 2. Calculate the price and process payment.
            double amount = show.getPricingStrategy().calcPrice(seats);
            Payment payment = paymentStrategy.pay(amount);
            
            if (payment == null || payment.getStatus() != PaymentStatus.SUCCESS) {
                System.out.println("Payment failed; releasing seat locks.");
                return Optional.empty();
            }

            String bookingId = UUID.randomUUID().toString();
            // 3. Confirm only if this user still owns every lock (no timeout/race).
            if (!seatLockMgr.confirmSeats(show, seats, user.getId(),bookingId)) {
                System.out.println("Seat lock expired or ownership changed; booking not confirmed.");
                return Optional.empty();
            }

            Booking booking = new Booking(bookingId, user, show, seats, amount, payment);
            bookings.put(booking.getId(), booking);

            return Optional.of(booking);

        } finally {
            // Releases locks on payment failure/exception. After confirmation, locks are already removed.
            seatLockMgr.unlockSeats(show, seats, user.getId());
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

        Show show = booking.getShow();

        synchronized (show) {
            if (booking.getStatus() != BookingStatus.CONFIRMED) {
                return false;
            }

            boolean released = releaseBookingSeats(
                show,
                booking.getShowSeats(),
                booking.getId()
            );

            if (!released) {
                return false;
            }

            booking.markCancelled();
            return true;
        }
    }

    // Release the seats if the seats is still owned by the same bookingId.
    public boolean releaseBookingSeats(
            Show show,
            List<ShowSeat> seats,
            String bookingId) {

        if (show == null || seats == null || seats.isEmpty() || bookingId == null || bookingId.isEmpty()) {
            return false;
        }

        synchronized (show) {
            Set<ShowSeat> uniqueSeats = new HashSet<>();

            // Validate every seat before changing anything.
            for (ShowSeat seat : seats) {
                if (!show.ownsShowSeat(seat)
                        || !uniqueSeats.add(seat)
                        || seat.getStatus() != SeatStatus.BOOKED
                        || !bookingId.equals(seat.getBookingId())) {
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

    public void shutdown() {
        seatLockMgr.shutdown();
    }
}
