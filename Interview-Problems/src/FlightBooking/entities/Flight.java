package FlightBooking.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import FlightBooking.enums.SeatStatus;
import FlightBooking.strategies.PricingStrategy;

public class Flight {
    private final String id;
    private final Aircraft aircraft;
    private final Airport source;
    private final Airport destination;
    private final LocalDateTime departureTime;
    private final LocalDateTime arrivalTime;
    private final PricingStrategy pricingStrategy;
    private final Map<String, FlightSeat> seatNumberToFlightSeat = new LinkedHashMap<>();

    public Flight(String id, Aircraft aircraft, Airport source, Airport destination,
                  LocalDateTime departureTime, LocalDateTime arrivalTime,
                  PricingStrategy pricingStrategy) {

        if (source.getCode().equals(destination.getCode())) {
            throw new IllegalArgumentException("Source and destination cannot be the same.");
        }
        if (!arrivalTime.isAfter(departureTime)) {
            throw new IllegalArgumentException("Arrival must be after departure.");
        }
        
        this.id = id;
        this.aircraft = aircraft;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.pricingStrategy = pricingStrategy;

        // Every flight gets its own availability records for the aircraft's seats.
        for (Seat seat : aircraft.getSeats()) {
            seatNumberToFlightSeat.put(seat.getSeatNumber(), new FlightSeat(id, seat));
        }
    }

    public FlightSeat getFlightSeat(String seatNumber) {
        return seatNumberToFlightSeat.get(seatNumber);
    }

    /** True only if this FlightSeat belongs to THIS flight (not another one). */
    public boolean ownsFlightSeat(FlightSeat flightSeat) {
        return flightSeat != null
            && seatNumberToFlightSeat.get(flightSeat.getSeatNumber()) == flightSeat;
    }

    public List<FlightSeat> getFlightSeats() {
        return new ArrayList<>(seatNumberToFlightSeat.values());
    }

    // synchronized so it sees seat changes made under this flight's lock
    public synchronized List<FlightSeat> getAvailableSeats() {
        List<FlightSeat> available = new ArrayList<>();
        for (FlightSeat fs : seatNumberToFlightSeat.values()) {
            if (fs.getStatus() == SeatStatus.AVAILABLE) {
                available.add(fs);
            }
        }
        return available;
    }

    public String getId() { return id; }
    public Aircraft getAircraft() { return aircraft; }
    public Airport getSource() { return source; }
    public Airport getDestination() { return destination; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public PricingStrategy getPricingStrategy() { return pricingStrategy; }
}
