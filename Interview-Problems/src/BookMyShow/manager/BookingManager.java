package manager;

import java.util.List;
import java.util.Optional;

import entities.Booking;
import entities.Payment;
import entities.Seat;
import entities.Show;
import entities.User;
import enums.PaymentStatus;
import strategies.PaymentStrategy;

public class BookingManager {
    private static BookingManager instance;
    SeatLockManager seatLockMgr;

    private BookingManager(){
        this.seatLockMgr = SeatLockManager.getInstance();
    }

    public static BookingManager getInstance(){
        if(BookingManager.instance == null){
            synchronized (BookingManager.class){
                if(BookingManager.instance == null){
                    BookingManager.instance = new BookingManager();
                }
            }
            
        }
        return BookingManager.instance;
    }

    public Optional<Booking> createBooking(User user, Show show, List<Seat> seats, PaymentStrategy paymentStrategy){
        // 1. Lock the seats;
        seatLockMgr.lockSeats(show, seats, user.getId());

        // process payment.
        // 2. calculate total payable amount.
        double amount = show.getPricingStrategy().calcPrice(seats);
        // 3. process payment;
        Payment payment = paymentStrategy.pay(amount);

        if(payment.getStatus() == PaymentStatus.SUCCESS){
            Booking booking = new Booking(user, show, seats, amount, payment);
            booking.confirmBooking();
            seatLockMgr.unlockSeats(show, seats, user.getId());
            return Optional.of(booking);
        }else{
            System.out.println("Payment Failed!");
            return Optional.empty();
        }
    }


    public void shutdown(){
        seatLockMgr.shutdown();
    }
}
