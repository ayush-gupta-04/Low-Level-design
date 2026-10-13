package HotelBooking.manager;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import HotelBooking.entities.Booking;
import HotelBooking.entities.DateRange;
import HotelBooking.entities.Guest;
import HotelBooking.entities.Hotel;
import HotelBooking.entities.Payment;
import HotelBooking.entities.Room;
import HotelBooking.enums.BookingStatus;
import HotelBooking.enums.PaymentStatus;
import HotelBooking.strategies.PaymentStrategy;

public class BookingManager {
    private static volatile BookingManager instance;
    private final RoomLockManager roomLockMgr;
    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();

    private BookingManager() {
        this.roomLockMgr = RoomLockManager.getInstance();
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


    // main methods
    public Optional<Booking> createBooking(Guest guest, Hotel hotel, Room room, DateRange stay, PaymentStrategy paymentStrategy) {

        if (guest == null || hotel == null || room == null || stay == null || paymentStrategy == null) {
            return Optional.empty();
        }

        // 1. Reserve the room before taking payment.
        Optional<String> lockId = roomLockMgr.lockRoom(hotel, room, stay, guest.getId());
        if (lockId.isEmpty()) {
            System.out.println("Could not lock the requested room.");
            return Optional.empty();
        }

        try {
            // 2. Calculate the price and process payment.
            double amount = stay.nights() * room.getPricePerNight();
            Payment payment = paymentStrategy.pay(amount);

            if (payment == null || payment.getStatus() != PaymentStatus.SUCCESS) {
                System.out.println("Payment failed; releasing room lock.");
                return Optional.empty();
            }

            String bookingId = UUID.randomUUID().toString();
            // 3. Confirm only if this guest still owns the lock (no timeout/race).
            if (!roomLockMgr.confirmRoom(hotel, room, lockId.get(), guest.getId(), bookingId)) {
                System.out.println("Room lock expired; booking not confirmed. Payment refunded.");
                return Optional.empty();
            }

            Booking booking = new Booking(bookingId, guest, hotel, room, stay, amount, payment);
            bookings.put(booking.getId(), booking);

            return Optional.of(booking);

        } finally {
            // Releases the lock on payment failure/exception. After confirmation it is already removed.
            roomLockMgr.unlockRoom(hotel, room, lockId.get());
        }
    }

    public boolean cancelBooking(String guestId, String bookingId) {
        if (guestId == null || bookingId == null) {
            return false;
        }

        Booking booking = bookings.get(bookingId);

        if (booking == null || !guestId.equals(booking.getGuest().getId())) {
            return false;
        }

        Room room = booking.getRoom();

        synchronized (room) {
            if (booking.getStatus() != BookingStatus.CONFIRMED) {
                return false;
            }
            // Cancellation is allowed only before the check-in date.
            if (!LocalDate.now().isBefore(booking.getStay().checkIn())) {
                return false;
            }
            // Free the dates only if they are still owned by this booking.
            if (!room.removeStay(booking.getId())) {
                return false;
            }

            booking.markCancelled();
            return true;
        }
    }

    public List<Booking> getBookingsForGuest(String guestId) {
        return bookings.values().stream()
            .filter(booking -> booking.getGuest().getId().equals(guestId))
            .toList();
    }

    public void shutdown() {
        roomLockMgr.shutdown();
    }
}
