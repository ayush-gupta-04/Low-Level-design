package entities;

import java.util.ArrayList;
import java.util.List;

public class Screen {
    private String id;
    private List<Seat> seats;

    public Screen(String id){
        this.seats = new ArrayList<>();
        this.id = id;
    }

    public void addSeat(Seat seat){
        seats.add(seat);
    }

    public String getId() { return id; }
    public List<Seat> getSeats() { return seats; }
}