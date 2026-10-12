package BookMyShow;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import BookMyShow.entities.Booking;
import BookMyShow.entities.Cinema;
import BookMyShow.entities.Movie;
import BookMyShow.entities.Payment;
import BookMyShow.entities.Screen;
import BookMyShow.entities.Seat;
import BookMyShow.entities.Show;
import BookMyShow.entities.ShowSeat;
import BookMyShow.entities.User;
import BookMyShow.enums.PaymentStatus;
import BookMyShow.enums.SeatStatus;
import BookMyShow.enums.SeatType;
import BookMyShow.manager.MovieTicketBookingSystem;
import BookMyShow.observer.UserObserver;
import BookMyShow.strategies.PaymentStrategy;
import BookMyShow.strategies.WeekdaysPricingStrategy;

public class Main {
    public static void main(String[] args) {
        MovieTicketBookingSystem service = MovieTicketBookingSystem.getInstance();

        // 1. Add movie and cinema.
        Movie matrix = service.addMovie("M1", "The Matrix", 120);
        Cinema pvr = service.addCinema("pvr-1", "PVR-Ranchi", "Ranchi");

        // 2. Create the physical seats for a screen.
        Screen screen1 = new Screen("S1");
        for (int i = 1; i <= 10; i++) {
            screen1.addSeat(new Seat("A" + i, 1, i, SeatType.SILVER));
            screen1.addSeat(new Seat("B" + i, 2, i,
                i <= 5 ? SeatType.SILVER : SeatType.PLATINUM));
            screen1.addSeat(new Seat("C" + i, 3, i,
                i <= 5 ? SeatType.PLATINUM : SeatType.GOLD));
        }
        pvr.addScreen(screen1);

        // 3. Two shows share the same screen and physical Seat objects.
        //    Each Show creates its own ShowSeat inventory records.
        Show afternoonShow = service.addShow(
            "show1", matrix, pvr, screen1, new WeekdaysPricingStrategy());
        Show eveningShow = service.addShow(
            "show2", matrix, pvr, screen1, new WeekdaysPricingStrategy());

        // User and observer setup.
        User alice = service.addUser("Alice", "alice@example.com");
        UserObserver aliceObserver = new UserObserver(alice);
        matrix.addObserver(aliceObserver);

        System.out.println("\n--- Notifying Observers about Movie Release ---");
        matrix.notifyObservers();

        // 4. Find available inventory for the selected show.
        Show selectedShow = afternoonShow;
        List<ShowSeat> availableSeats = selectedShow.getShowSeats().stream()
            .filter(showSeat -> showSeat.getStatus() == SeatStatus.AVAILABLE)
            .toList();

        System.out.println("\nAvailable seats for " + selectedShow.getId() + ": ");
        System.out.println(availableSeats.stream().map(ShowSeat::getSeatId).toList());

        // Select A1 and A2 from this show's own ShowSeat inventory.
        List<ShowSeat> desiredSeats = List.of(
            selectedShow.getShowSeatBySeatId("A1"),
            selectedShow.getShowSeatBySeatId("A2")
        );
        System.out.println("Alice selects seats: "
            + desiredSeats.stream().map(ShowSeat::getSeatId).toList());

        // 5. Book tickets.
        // Use deterministic success in the demo so the BOOKED vs AVAILABLE
        // distinction is visible every time. The random simulator remains available
        // in CreditCardPaymentStrategy for payment-failure flows.
        PaymentStrategy demoPayment = amount -> new Payment(
            amount, PaymentStatus.SUCCESS, "DEMO_TXN_" + UUID.randomUUID());
        Optional<Booking> bookingOpt = service.bookTickets(
            alice.getId(), selectedShow.getId(), desiredSeats, demoPayment);

        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            System.out.println("\n--- Booking Successful! ---");
            System.out.println("Booking ID: " + booking.getId());
            System.out.println("User: " + booking.getUser().getName());
            System.out.println("Movie: " + booking.getShow().getMovie().getName());
            System.out.println("Seats: "
                + booking.getShowSeats().stream().map(ShowSeat::getSeatId).toList());
            System.out.println("Total Amount: $" + booking.getAmount());
            System.out.println("Payment Status: " + booking.getPayment().getStatus());
        } else {
            System.out.println("Booking failed.");
        }

        // 6. Verify the same physical seat has independent state in each show.
        System.out.println("\nSeat status in afternoon show:");
        desiredSeats.forEach(showSeat -> System.out.printf(
            "Seat %s status: %s%n", showSeat.getSeatId(), showSeat.getStatus()));

        System.out.println("\nSeat status in evening show (same physical seats):");
        desiredSeats.forEach(afternoonSeat -> {
            ShowSeat eveningSeat = eveningShow.getShowSeat(afternoonSeat.getSeat());
            System.out.printf("Seat %s status: %s%n",
                eveningSeat.getSeatId(), eveningSeat.getStatus());
        });

        service.shutdown();
    }
}
