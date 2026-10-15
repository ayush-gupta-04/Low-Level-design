package FlightBooking.manager;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import FlightBooking.entities.Airport;
import FlightBooking.entities.Flight;

/** Stores flights and answers flight searches. */
public class FlightManager {
    private final Map<String, Flight> flights = new HashMap<>();

    public void addFlight(Flight flight) {
        flights.put(flight.getId(), flight);
    }

    public Flight getFlight(String flightId) {
        return flights.get(flightId);
    }

    /** Flights from source to destination departing on the given date, earliest first. */
    public List<Flight> searchFlights(Airport source, Airport destination, LocalDate date) {
        return flights.values().stream()
            .filter(f -> f.getSource().getCode().equals(source.getCode()))
            .filter(f -> f.getDestination().getCode().equals(destination.getCode()))
            .filter(f -> f.getDepartureTime().toLocalDate().equals(date))
            .sorted(Comparator.comparing(Flight::getDepartureTime))
            .toList();
    }
}
