package FlightBooking;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import FlightBooking.entities.Aircraft;
import FlightBooking.entities.Airport;
import FlightBooking.entities.Booking;
import FlightBooking.entities.Flight;
import FlightBooking.entities.FlightSeat;
import FlightBooking.entities.Passenger;
import FlightBooking.entities.Payment;
import FlightBooking.entities.Seat;
import FlightBooking.enums.BookingStatus;
import FlightBooking.enums.PaymentMethod;
import FlightBooking.enums.PaymentStatus;
import FlightBooking.enums.SeatClass;
import FlightBooking.enums.SeatPosition;
import FlightBooking.enums.SeatStatus;
import FlightBooking.strategies.PaymentStrategy;
import FlightBooking.strategies.pricing.PeakSeasonPricingStrategy;
import FlightBooking.strategies.pricing.StandardPricingStrategy;

/**
 * Deterministic smoke/integration tests for FlightBookingSystem.
 * Run from the project root with: java FlightBooking.Main
 */
public class Main {

    private static int passedChecks = 0;

    public static void main(String[] args) throws InterruptedException {
        runTests();
        System.out.println("\nALL TESTS PASSED: " + passedChecks + " checks.");
    }

    private static void runTests() throws InterruptedException {
        FlightBookingSystem system = FlightBookingSystem.getInstance();

        // 1. Test data setup.
        Airport del = system.addAirport(
                "DEL", "Indira Gandhi International", "Delhi");
        Airport bom = system.addAirport(
                "BOM", "Chhatrapati Shivaji Maharaj International", "Mumbai");
        Airport blr = system.addAirport(
                "BLR", "Kempegowda International", "Bengaluru");

        // Both flights use the same aircraft layout, but each flight owns
        // independent FlightSeat objects and independent inventory.
        Aircraft a320 = system.addAircraft(
                createAircraft("AC1", "Airbus A320"));
        Aircraft b737 = system.addAircraft(
                createAircraft("AC2", "Boeing 737"));

        LocalDate travelDate = LocalDate.of(2026, 11, 15);
        Flight ai101 = system.addFlight(
                "AI101", a320, del, bom,
                travelDate.atTime(8, 0), travelDate.atTime(10, 15),
                new StandardPricingStrategy());
        Flight ai102 = system.addFlight(
                "AI102", a320, del, bom,
                travelDate.atTime(18, 0), travelDate.atTime(20, 15),
                new PeakSeasonPricingStrategy());
        system.addFlight(
                "AI201", b737, del, blr,
                travelDate.atTime(9, 0), travelDate.atTime(11, 45),
                new StandardPricingStrategy());

        // Deterministic test payment strategies: the production strategies
        // use Math.random(), which would make integration tests flaky.
        PaymentStrategy successfulUpi = successfulPayment(PaymentMethod.UPI);
        PaymentStrategy successfulCard = successfulPayment(PaymentMethod.CARD);
        PaymentStrategy failedCard = failedPayment(PaymentMethod.CARD);

        // 2. Flight search returns DEL -> BOM flights for the selected date,
        // ordered by departure time.
        List<Flight> searchResults = system.searchFlights("DEL", "BOM", travelDate);
        check("Search returns both DEL -> BOM flights", searchResults.size() == 2);
        check("Search results are ordered by departure time",
                searchResults.get(0).getId().equals("AI101")
                        && searchResults.get(1).getId().equals("AI102"));

        // 3. Create passengers/bookers.
        Passenger alice = system.addPassenger("Alice", 28, "alice@example.com");
        Passenger bob = system.addPassenger("Bob", 31, "9876543210");
        Passenger charlie = system.addPassenger("Charlie", 45, "charlie@example.com");

        // 4. Alice books two seats, one for herself and one for Bob.
        Optional<Booking> aliceBookingResult = system.bookFlight(
                alice, "AI101", List.of(alice, bob), List.of("5A", "5B"), successfulUpi);
        check("Two-seat booking succeeds", aliceBookingResult.isPresent());

        Booking aliceBooking = aliceBookingResult.orElseThrow();
        check("Booking identifies Alice as the booker",
                aliceBooking.getUser().getId().equals(alice.getId()));
        check("Booking confirms the correct passengers and seats",
                aliceBooking.getPassengers().size() == 2
                        && aliceBooking.getFlightSeats().size() == 2
                        && aliceBooking.getSeatAssignments().get(ai101.getFlightSeat("5A")).getId().equals(alice.getId())
                        && aliceBooking.getSeatAssignments().get(ai101.getFlightSeat("5B")).getId().equals(bob.getId()));
        check("Both requested seats become BOOKED",
                ai101.getFlightSeat("5A").getStatus() == SeatStatus.BOOKED
                        && ai101.getFlightSeat("5B").getStatus() == SeatStatus.BOOKED);
        check("Two economy seats cost Rs 10000 on standard pricing",
                Math.abs(aliceBooking.getAmount() - 10000.0) < 0.001);

        // 5. Prevent double booking and verify rejected multi-seat requests do
        // not partially reserve any otherwise-available seat.
        Optional<Booking> duplicateSeatResult = system.bookFlight(
                charlie, "AI101", List.of(charlie), List.of("5A"), successfulUpi);
        check("Another user cannot book an already-booked seat", duplicateSeatResult.isEmpty());

        Optional<Booking> partialLockResult = system.bookFlight(
                charlie, "AI101", List.of(charlie, system.addPassenger("Diana", 26, "diana@example.com")),
                List.of("5A", "6B"), successfulUpi);
        check("Multi-seat request containing a booked seat is rejected", partialLockResult.isEmpty());
        check("Rejected multi-seat request does not partially lock 6B",
                ai101.getFlightSeat("6B").getStatus() == SeatStatus.AVAILABLE);

        // 6. Payment failure should not confirm a booking and must release its hold.
        Optional<Booking> failedPaymentResult = system.bookFlight(
                charlie, "AI101", List.of(charlie), List.of("6A"), failedCard);
        check("Failed payment does not create a booking", failedPaymentResult.isEmpty());
        check("Failed payment releases the seat back to AVAILABLE",
                ai101.getFlightSeat("6A").getStatus() == SeatStatus.AVAILABLE);

        // 7. Same seat number on a different flight is independent, even when
        // both flights use the same aircraft.
        Optional<Booking> secondFlightResult = system.bookFlight(
                charlie, "AI102", List.of(charlie), List.of("5A"), successfulCard);
        check("Same seat number can be booked on a different flight", secondFlightResult.isPresent());
        Booking secondFlightBooking = secondFlightResult.orElseThrow();
        check("Second flight uses its own seat inventory",
                secondFlightBooking.getFlight().getId().equals("AI102")
                        && ai102.getFlightSeat("5A").getStatus() == SeatStatus.BOOKED
                        && ai101.getFlightSeat("5A").getStatus() == SeatStatus.BOOKED);
        check("Peak pricing applies the 20% surcharge to an economy seat",
                Math.abs(secondFlightBooking.getAmount() - 6000.0) < 0.001);

        // 8. Seat class affects fare.
        Optional<Booking> businessResult = system.bookFlight(
                charlie, "AI101", List.of(charlie), List.of("3C"), successfulCard);
        check("Business-class booking succeeds", businessResult.isPresent());
        check("Business-class seat uses the correct standard fare",
                Math.abs(businessResult.orElseThrow().getAmount() - 12000.0) < 0.001);

        // 9. Only the booker can cancel. Cancellation is idempotent in the
        // sense that repeating it must not succeed a second time.
        check("A passenger who is not the booker cannot cancel the booking",
                !system.cancelBooking(bob.getId(), aliceBooking.getId()));
        check("The booker can cancel the booking",
                system.cancelBooking(alice.getId(), aliceBooking.getId()));
        check("A cancelled booking cannot be cancelled a second time",
                !system.cancelBooking(alice.getId(), aliceBooking.getId()));
        check("Booking status changes to CANCELLED",
                aliceBooking.getStatus() == BookingStatus.CANCELLED);
        check("Cancellation releases all seats from that booking",
                ai101.getFlightSeat("5A").getStatus() == SeatStatus.AVAILABLE
                        && ai101.getFlightSeat("5B").getStatus() == SeatStatus.AVAILABLE);

        // 10. Released inventory can be booked by another user.
        Optional<Booking> rebookResult = system.bookFlight(
                charlie, "AI101", List.of(charlie), List.of("5A"), successfulUpi);
        check("A released seat can be booked again", rebookResult.isPresent());
        check("Rebooked seat is BOOKED by the new booking",
                ai101.getFlightSeat("5A").getStatus() == SeatStatus.BOOKED
                        && rebookResult.orElseThrow().getId()
                                .equals(ai101.getFlightSeat("5A").getBookingId()));

        // 11. Race two independent users for one currently-available seat.
        // A latch releases both threads together to make the race meaningful.
        AtomicInteger successfulBookings = new AtomicInteger();
        AtomicInteger failedThreads = new AtomicInteger();
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        Runnable bobRace = () -> tryBookSeatAtRaceStart(
                system, bob, successfulUpi, ready, start, successfulBookings, failedThreads);
        Runnable charlieRace = () -> tryBookSeatAtRaceStart(
                system, charlie, successfulUpi, ready, start, successfulBookings, failedThreads);

        Thread t1 = new Thread(bobRace, "bob-seat-race");
        Thread t2 = new Thread(charlieRace, "charlie-seat-race");
        t1.start();
        t2.start();
        ready.await();
        start.countDown();
        t1.join();
        t2.join();

        check("Concurrent race has no uncaught thread failures", failedThreads.get() == 0);
        check("Exactly one concurrent booking for seat 7F succeeds",
                successfulBookings.get() == 1);
        check("Seat 7F ends in BOOKED state", ai101.getFlightSeat("7F").getStatus() == SeatStatus.BOOKED);
    }

    private static void tryBookSeatAtRaceStart(
            FlightBookingSystem system,
            Passenger booker,
            PaymentStrategy paymentStrategy,
            CountDownLatch ready,
            CountDownLatch start,
            AtomicInteger successfulBookings,
            AtomicInteger failedThreads) {
        try {
            ready.countDown();
            start.await();
            Optional<Booking> result = system.bookFlight(
                    booker, "AI101", List.of(booker), List.of("7F"), paymentStrategy);
            if (result.isPresent()) {
                successfulBookings.incrementAndGet();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            failedThreads.incrementAndGet();
        } catch (RuntimeException e) {
            e.printStackTrace();
            failedThreads.incrementAndGet();
        }
    }

    private static PaymentStrategy successfulPayment(PaymentMethod method) {
        return amount -> new Payment(
                amount, method, PaymentStatus.SUCCESS,
                "TEST-" + method + "-" + UUID.randomUUID());
    }

    private static PaymentStrategy failedPayment(PaymentMethod method) {
        return amount -> new Payment(
                amount, method, PaymentStatus.FAILED,
                "TEST-" + method + "-FAILED-" + UUID.randomUUID());
    }

    private static Aircraft createAircraft(String id, String model) {
        Aircraft aircraft = new Aircraft(id, model);
        char[] columns = {'A', 'B', 'C', 'D', 'E', 'F'};
        SeatPosition[] positions = {
                SeatPosition.WINDOW, SeatPosition.MIDDLE, SeatPosition.AISLE,
                SeatPosition.AISLE, SeatPosition.MIDDLE, SeatPosition.WINDOW
        };

        for (int row = 1; row <= 7; row++) {
            SeatClass seatClass = row <= 2 ? SeatClass.FIRST_CLASS
                    : row <= 4 ? SeatClass.BUSINESS
                    : SeatClass.ECONOMY;

            for (int i = 0; i < columns.length; i++) {
                aircraft.addSeat(new Seat("" + row + columns[i], seatClass, positions[i]));
            }
        }
        return aircraft;
    }

    private static void check(String description, boolean condition) {
        if (!condition) {
            throw new AssertionError("FAILED: " + description);
        }
        passedChecks++;
        System.out.println("[PASS] " + description);
    }

    @SuppressWarnings("unused")
    private static void printBooking(Booking booking) {
        System.out.println("Booking ID : " + booking.getId() + " (" + booking.getStatus() + ")");
        System.out.println("Booker     : " + booking.getUser().getName());
        System.out.println("Flight     : " + booking.getFlight().getId() + " "
                + booking.getFlight().getSource().getCode() + " -> "
                + booking.getFlight().getDestination().getCode());
        for (Map.Entry<FlightSeat, Passenger> entry : booking.getSeatAssignments().entrySet()) {
            FlightSeat seat = entry.getKey();
            Passenger passenger = entry.getValue();
            System.out.println("Passenger  : " + passenger.getName()
                    + " -> seat " + seat.getSeatNumber()
                    + " (" + seat.getSeat().getSeatClass()
                    + ", " + seat.getSeat().getPosition() + ")");
        }
        System.out.println("Total fare : Rs " + booking.getAmount());
    }
}
