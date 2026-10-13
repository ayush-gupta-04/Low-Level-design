package FlightBooking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import FlightBooking.entities.Aircraft;
import FlightBooking.entities.Airport;
import FlightBooking.entities.Booking;
import FlightBooking.entities.Flight;
import FlightBooking.entities.FlightSeat;
import FlightBooking.entities.Passenger;
import FlightBooking.entities.Seat;
import FlightBooking.enums.SeatClass;
import FlightBooking.enums.SeatPosition;
import FlightBooking.manager.FlightBookingSystem;
import FlightBooking.strategies.PaymentStrategy;
import FlightBooking.strategies.payment.CardPaymentStrategy;
import FlightBooking.strategies.payment.UpiPaymentStrategy;
import FlightBooking.strategies.pricing.PeakSeasonPricingStrategy;
import FlightBooking.strategies.pricing.StandardPricingStrategy;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        FlightBookingSystem system = FlightBookingSystem.getInstance();

        // 1. Airports
        Airport del = system.addAirport("DEL", "Indira Gandhi International", "Delhi");
        Airport bom = system.addAirport("BOM", "Chhatrapati Shivaji Maharaj International", "Mumbai");
        Airport blr = system.addAirport("BLR", "Kempegowda International", "Bengaluru");

        // 2. Aircraft (with seat layout)
        Aircraft a320 = system.addAircraft(createAircraft("AC1", "Airbus A320"));
        Aircraft b737 = system.addAircraft(createAircraft("AC2", "Boeing 737"));

        // 3. Flights (AI101 and AI102 share the SAME aircraft, yet every flight tracks its own seat availability)
        LocalDate travelDate = LocalDate.of(2026, 11, 15);
        system.addFlight("AI101", a320, del, bom,
            travelDate.atTime(8, 0), travelDate.atTime(10, 15), new StandardPricingStrategy());
        system.addFlight("AI102", a320, del, bom,
            travelDate.atTime(18, 0), travelDate.atTime(20, 15), new PeakSeasonPricingStrategy());
        system.addFlight("AI201", b737, del, blr,
            travelDate.atTime(9, 0), travelDate.atTime(11, 45), new StandardPricingStrategy());

        // 4. Search flights: DEL -> BOM on 15 Nov 2026
        System.out.println("--- Search: DEL -> BOM on " + travelDate + " ---");
        List<Flight> results = system.searchFlights("DEL", "BOM", travelDate);
        Flight ai101 = results.get(0);
        for (Flight f : results) {
            System.out.println(f.getId() + " | " + f.getAircraft().getModel()
                + " | dep " + f.getDepartureTime().toLocalTime()
                + " | arr " + f.getArrivalTime().toLocalTime()
                + " | available seats: " + f.getAvailableSeats().size()
                + "/" + f.getAircraft().getCapacity());
        }

        // 5. Passengers
        Passenger alice = system.addPassenger("Alice", 28, "alice@example.com");
        Passenger bob = system.addPassenger("Bob", 31, "9876543210");
        Passenger charlie = system.addPassenger("Charlie", 45, "charlie@example.com");

        // 6. Book 2 seats on AI101 using UPI (multiple passengers in one booking)
        System.out.println("\n--- Alice + Bob book 5A, 5B on AI101 (UPI) ---");
        Optional<Booking> b1 = system.bookFlight("AI101", List.of(alice, bob),
            List.of("5A", "5B"), new UpiPaymentStrategy());
        b1.ifPresent(Main::printBooking);

        // 7. Double booking: Charlie tries seat 5A on the same flight
        System.out.println("\n--- Charlie tries 5A on AI101 (already booked) ---");
        Optional<Booking> doubleBooking = system.bookFlight("AI101", List.of(charlie),
            List.of("5A"), new UpiPaymentStrategy());
        System.out.println("Booked? " + doubleBooking.isPresent());

        // 8. Failed payment must not confirm the booking, and the seat must be freed
        System.out.println("\n--- Charlie books 6A on AI101 with an INVALID card ---");
        Optional<Booking> failed = system.bookFlight("AI101", List.of(charlie),
            List.of("6A"), new CardPaymentStrategy());
        System.out.println("Booked? " + failed.isPresent());
        System.out.println("Seat 6A status: " + ai101.getFlightSeat("6A").getStatus());

        // 9. Same seat number on a different flight is independent
        System.out.println("\n--- Charlie books 5A on AI102 (peak pricing, card) ---");
        Optional<Booking> b2 = system.bookFlight("AI102", List.of(charlie),
            List.of("5A"), new CardPaymentStrategy());
        b2.ifPresent(Main::printBooking);

        // 10. Business class seat (fare depends on seat class)
        System.out.println("\n--- Charlie books 3C (Business) on AI101 (card) ---");
        Optional<Booking> b3 = system.bookFlight("AI101", List.of(charlie),
            List.of("3C"), new CardPaymentStrategy());
        b3.ifPresent(Main::printBooking);

        // 11. Cancel Alice + Bob's booking -> seats are released
        System.out.println("\n--- Cancel booking " + b1.get().getId() + " ---");
        System.out.println("Cancelled? " + system.cancelBooking(b1.get().getId()));
        System.out.println("Cancel again? " + system.cancelBooking(b1.get().getId()));
        System.out.println("Booking status: " + b1.get().getStatus());
        System.out.println("Seat 5A status: " + ai101.getFlightSeat("5A").getStatus());

        // 12. Charlie can now book the released seat
        System.out.println("\n--- Charlie books the released seat 5A on AI101 ---");
        Optional<Booking> b4 = system.bookFlight("AI101", List.of(charlie),
            List.of("5A"), new UpiPaymentStrategy());
        System.out.println("Booked? " + b4.isPresent());

        // 13. Two threads race for the same seat: exactly one must win
        System.out.println("\n--- Race: two threads try to book 7F on AI101 ---");
        AtomicInteger winners = new AtomicInteger();
        PaymentStrategy upi = new UpiPaymentStrategy();
        Runnable task = () -> {
            Optional<Booking> result = system.bookFlight("AI101", List.of(bob), List.of("7F"), upi);
            if (result.isPresent()) {
                winners.incrementAndGet();
            }
        };
        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("Successful bookings for 7F: " + winners.get() + " (expected 1)");
    }

    /** 7 rows x 6 columns: rows 1-2 First, rows 3-4 Business, rows 5-7 Economy. */
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

    private static void printBooking(Booking booking) {
        System.out.println("Booking ID : " + booking.getId() + " (" + booking.getStatus() + ")");
        System.out.println("Flight     : " + booking.getFlight().getId() + " "
            + booking.getFlight().getSource().getCode() + " -> "
            + booking.getFlight().getDestination().getCode());
        for (int i = 0; i < booking.getPassengers().size(); i++) {
            FlightSeat seat = booking.getFlightSeats().get(i);
            System.out.println("Passenger  : " + booking.getPassengers().get(i).getName()
                + " -> seat " + seat.getSeatNumber()
                + " (" + seat.getSeat().getSeatClass() + ", " + seat.getSeat().getPosition() + ")");
        }
        System.out.println("Total fare : Rs " + booking.getTotalFare());
        System.out.println("Payment    : " + booking.getPayment().getMethod()
            + " " + booking.getPayment().getStatus());
    }
}
