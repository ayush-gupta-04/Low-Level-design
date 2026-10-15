package FlightBooking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import FlightBooking.entities.Aircraft;
import FlightBooking.entities.Airport;
import FlightBooking.entities.Booking;
import FlightBooking.entities.Flight;
import FlightBooking.entities.FlightSeat;
import FlightBooking.entities.Passenger;
import FlightBooking.manager.BookingManager;
import FlightBooking.manager.FlightManager;
import FlightBooking.strategies.PaymentStrategy;
import FlightBooking.strategies.PricingStrategy;

/** Single entry point (facade) of the system. */
public class FlightBookingSystem {
    private static FlightBookingSystem instance;

    private final Map<String, Airport> airports = new HashMap<>();
    private final Map<String, Aircraft> aircrafts = new HashMap<>();
    private final Map<String, Passenger> passengers = new HashMap<>();

    private final FlightManager flightManager = new FlightManager();
    private final BookingManager bookingManager = BookingManager.getInstance();

    private FlightBookingSystem() { }

    public static synchronized FlightBookingSystem getInstance() {
        if (instance == null) {
            instance = new FlightBookingSystem();
        }
        return instance;
    }

    // ---------- setup ----------
    public Airport addAirport(String code, String name, String city) {
        Airport airport = new Airport(code, name, city);
        airports.put(code, airport);
        return airport;
    }

    public Aircraft addAircraft(Aircraft aircraft) {
        aircrafts.put(aircraft.getId(), aircraft);
        return aircraft;
    }

    public Flight addFlight(String id, Aircraft aircraft, Airport source, Airport destination, LocalDateTime departure, LocalDateTime arrival, PricingStrategy pricing) {
        Flight flight = new Flight(id, aircraft, source, destination, departure, arrival, pricing);
        flightManager.addFlight(flight);
        return flight;
    }

    public Passenger addPassenger(String name, int age, String contact) {
        Passenger passenger = new Passenger(name, age, contact);
        passengers.put(passenger.getId(), passenger);
        return passenger;
    }

    // ---------- main operations ----------
    public List<Flight> searchFlights(String sourceCode, String destinationCode, LocalDate date) {
        return flightManager.searchFlights(airports.get(sourceCode), airports.get(destinationCode), date);
    }

    public List<FlightSeat> getAvailableSeats(String flightId) {
        return flightManager.getFlight(flightId).getAvailableSeats();
    }

    /** seatNumbers.get(i) is the seat for passengers.get(i). */
    public Optional<Booking> bookFlight(Passenger user, String flightId, List<Passenger> passengers, List<String> seatNumbers, PaymentStrategy paymentStrategy) {
        Flight flight = flightManager.getFlight(flightId);
        Map<FlightSeat, Passenger> assignment = new HashMap<>();

        for (int i = 0; i < seatNumbers.size(); i++) {
            FlightSeat seat = flight.getFlightSeat(seatNumbers.get(i));
            assignment.put(seat, passengers.get(i));
        }
        return bookingManager.createBooking(user, flight, assignment, paymentStrategy);
    }

    public boolean cancelBooking(String userId, String bookingId) {
        return bookingManager.cancelBooking(userId, bookingId);
    }
}
