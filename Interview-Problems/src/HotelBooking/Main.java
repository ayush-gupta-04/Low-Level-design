package HotelBooking;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import HotelBooking.entities.ACRoom;
import HotelBooking.entities.Booking;
import HotelBooking.entities.Guest;
import HotelBooking.entities.Hotel;
import HotelBooking.entities.NonACRoom;
import HotelBooking.entities.Payment;
import HotelBooking.enums.PaymentStatus;
import HotelBooking.enums.RoomType;
import HotelBooking.manager.HotelBookingSystem;
import HotelBooking.strategies.PaymentStrategy;
import HotelBooking.strategies.UpiPaymentStrategy;

public class Main {
    public static void main(String[] args) {
        HotelBookingSystem service = HotelBookingSystem.getInstance();

        // 1. Add hotel and rooms.
        Hotel hotel = service.addHotel("H1", "Green Park Hotel", "12 Main Road", "Ranchi");
        service.addRoom("H1", new ACRoom("R1", "101", RoomType.DOUBLE, 2500.0, 2));
        service.addRoom("H1", new NonACRoom("R2", "102", RoomType.SINGLE, 1200.0, 1));

        // A room ID can be reused by another hotel. Availability stays hotel-scoped.
        service.addHotel("H2", "Lake View Hotel", "8 Lake Road", "Ranchi");
        service.addRoom("H2", new ACRoom("R1", "201", RoomType.DOUBLE, 2800.0, 2));

        // 2. Guests.
        Guest alice = service.addGuest("Alice", "alice@example.com");
        Guest bob = service.addGuest("Bob", "bob@example.com");

        // 3. Search hotels.
        System.out.println("Hotels in Ranchi: "
            + service.searchHotelsByCity("ranchi").stream().map(Hotel::getName).toList());

        LocalDate checkIn = LocalDate.now().plusDays(7);
        LocalDate checkOut = checkIn.plusDays(3);
        System.out.println("Room R1 available: " + service.isRoomAvailable("H1", "R1", checkIn, checkOut));

        // 4. Book a room (payment strategy is chosen by the caller).
        Optional<Booking> bookingOpt = service.bookRoom(
            alice.getId(), "H1", "R1", checkIn, checkOut, new UpiPaymentStrategy());

        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            System.out.println("\n--- Booking Successful! ---");
            System.out.println("Booking ID: " + booking.getId());
            System.out.println("Guest: " + booking.getGuest().getName());
            System.out.println("Hotel: " + booking.getHotel().getName() + " (" + booking.getHotel().getId() + ")");
            System.out.println("Room: " + booking.getRoom().getRoomNumber());
            System.out.println("Stay: " + booking.getStay().checkIn() + " to " + booking.getStay().checkOut());
            System.out.println("Total Amount: INR " + booking.getAmount());
            System.out.println("Payment Status: " + booking.getPayment().getStatus());
        } else {
            System.out.println("Booking failed.");
        }

        // 5. Same dates are no longer available for another guest.
        System.out.println("\nH1/R1 available now: " + service.isRoomAvailable("H1", "R1", checkIn, checkOut));
        System.out.println("H2/R1 (same room ID, different hotel) available: "
            + service.isRoomAvailable("H2", "R1", checkIn, checkOut));
        System.out.println("Bob tries the same dates: "
            + service.bookRoom(bob.getId(), "H1", "R1", checkIn, checkOut, new UpiPaymentStrategy()).isPresent());

        // 6. A failed payment does not create a booking and frees the room.
        PaymentStrategy failingPayment = amount -> new Payment(
            amount, PaymentStatus.FAILED, "FAILED_TXN_" + UUID.randomUUID());
        System.out.println("\nBob pays for R2 with a failing payment: "
            + service.bookRoom(bob.getId(), "H1", "R2", checkIn, checkOut, failingPayment).isPresent());
        System.out.println("Room R2 available: " + service.isRoomAvailable("H1", "R2", checkIn, checkOut));

        // 7. Cancel and book again.
        if (bookingOpt.isPresent()) {
            boolean cancelled = service.cancelBooking(alice.getId(), bookingOpt.get().getId());
            System.out.println("\nCancelled: " + cancelled
                + ", booking status: " + bookingOpt.get().getStatus()
                + ", payment status: " + bookingOpt.get().getPayment().getStatus());
            System.out.println("Room R1 available again: " + service.isRoomAvailable("H1", "R1", checkIn, checkOut));
        }

        service.shutdown();
    }
}
