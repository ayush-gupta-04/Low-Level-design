package FlightBooking.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Aircraft {
    private final String id;
    private final String model;
    private final List<Seat> seats = new ArrayList<>();   // the seat layout

    public Aircraft(String id, String model) {
        this.id = id;
        this.model = model;
    }

    public void addSeat(Seat seat) {
        seats.add(seat);
    }

    public String getId() { return id; }
    public String getModel() { return model; }
    public int getCapacity() { return seats.size(); }
    public List<Seat> getSeats() { return Collections.unmodifiableList(seats); }
}
