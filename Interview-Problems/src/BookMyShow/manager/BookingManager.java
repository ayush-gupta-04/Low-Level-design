package manager;

import java.util.List;
import java.util.Optional;

import entities.Booking;
import entities.Payment;
import entities.Show;
import entities.ShowSeat;
import entities.User;
import enums.PaymentStatus;
import strategies.PaymentStrategy;

public class BookingManager {
    private static BookingManager instance;
    private final SeatLockManager seatLockMgr;

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

            // 3. Confirm only if this user still owns every lock (no timeout/race).
            if (!seatLockMgr.confirmSeats(show, seats, user.getId())) {
                // Production systems must void/refund the successful payment here.
                System.out.println("Seat lock expired or ownership changed; booking not confirmed.");
                return Optional.empty();
            }

            return Optional.of(new Booking(user, show, seats, amount, payment));
        } finally {
            // Releases locks on payment failure/exception. After confirmation, locks are already removed.
            seatLockMgr.unlockSeats(show, seats, user.getId());
        }
    }

    public void shutdown() {
        seatLockMgr.shutdown();
    }
}
