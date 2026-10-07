import java.util.List;
import java.util.Optional;

import entities.Booking;
import entities.Cinema;
import entities.Movie;
import entities.Screen;
import entities.Seat;
import entities.Show;
import entities.User;
import enums.SeatStatus;
import enums.SeatType;
import manager.MovieTicketBookingSystem;
import observer.UserObserver;
import strategies.CreditCardPaymentStrategy;
import strategies.WeekdaysPricingStrategy;

public class Main {
    public static void main(String[] args) {
        MovieTicketBookingSystem service = MovieTicketBookingSystem.getInstance();

        // 1. Add movies
        Movie matrix = service.addMovie("M1", "The Matrix", 120);

        // 2. Add cinema.
        Cinema pvr = service.addCinema("pvr-1", "PVR-Ranchi", "Ranchi");

        // 3. Add Seats for a Screen
        Screen screen1 = new Screen("S1");
        for (int i = 1; i <= 10; i++) {
            screen1.addSeat(new Seat("A" + i, 1, i, SeatType.SILVER));
            screen1.addSeat(new Seat("B" + i, 2, i, i <= 5 ? SeatType.SILVER : SeatType.PLATINUM));
            screen1.addSeat(new Seat("C" + i, 2, i, i <= 5 ? SeatType.PLATINUM : SeatType.GOLD));
        }
        pvr.addScreen(screen1);


        // 4. add shows.
        Show matrixShow = service.addShow("show1", matrix, pvr, screen1, new WeekdaysPricingStrategy());



        // --- User and Observer Setup ---
        // add user.
        User alice = service.addUser("Alice", "alice@example.com");
        UserObserver aliceObserver = new UserObserver(alice);
        matrix.addObserver(aliceObserver);

        // Simulate movie release
        System.out.println("\n--- Notifying Observers about Movie Release ---");
        matrix.notifyObservers();


        // booking flow.
        System.out.println("\n--- Alice's Booking Flow ---");

        // 1. Search for shows
        List<Show> shows = service.findShowsInCinema(pvr);
        if(shows.isEmpty()){
            System.out.println("No shows in : " + pvr.getCity() + " " + pvr.getName());
        }
        Show selectedShow = shows.get(0);


        // 2. View available seats
        List<Seat> availableSeats = selectedShow.getScreen().getSeats().stream()
        .filter(seat -> seat.getStatus()==SeatStatus.AVAILABLE)
        .toList();
        
        System.out.println("Available seats are ... ");
        System.out.println(availableSeats.stream().map(seat -> seat.getId()).toList());

        // 3. select seats
        List<Seat> desiredSeats = List.of(availableSeats.get(2), availableSeats.get(3));
        System.out.println("Alice selects seats: " + desiredSeats.stream().map(seat -> seat.getId()).toList());


        // 4. Book Tickets
        Optional<Booking> bookingOpt = service.bookTickets(
                alice.getId(),
                selectedShow.getId(),
                desiredSeats,
                new CreditCardPaymentStrategy()
        );

        
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            System.out.println("\n--- Booking Successful! ---");
            System.out.println("Booking ID: " + booking.getId());
            System.out.println("User: " + booking.getUser().getName());
            System.out.println("Movie: " + booking.getShow().getMovie().getName());
            System.out.println("Seats: " + booking.getSeats().stream().map(s -> s.getId()).toList());
            System.out.println("Total Amount: $" + booking.getAmount());
            System.out.println("Payment Status: " + booking.getPayment().getStatus());
        } else {
            System.out.println("Booking failed.");
        }

        // 5. Verify seat status after booking
        System.out.println("\nSeat status after Alice's booking:");
        desiredSeats.forEach(seat -> System.out.printf("Seat %s status: %s%n", seat.getId(), seat.getStatus()));

        service.shutdown();
    }
}
