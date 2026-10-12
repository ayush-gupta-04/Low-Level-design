package BookMyShow.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import BookMyShow.strategies.PricingStrategy;

public class Show {
    private final String id;
    private final Movie movie;
    private final Screen screen;
    private final PricingStrategy pricingStrategy;
    private final Cinema cinema;
    private final Map<String, ShowSeat> seatIdToShowSeat;

    public Show(String id, Movie movie, Cinema cinema, Screen screen, PricingStrategy pricingStrategy) {
        this.id = id;
        this.movie = movie;
        this.screen = screen;
        this.pricingStrategy = pricingStrategy;
        this.cinema = cinema;
        this.seatIdToShowSeat = new HashMap<>();

        // Every show gets separate availability records for the same physical seats.
        for (Seat seat : screen.getSeats()) {
            seatIdToShowSeat.putIfAbsent(seat.getId(), new ShowSeat(id, seat));
        }
    }

    /** Finds this show's inventory record for a physical seat on its screen. */
    public ShowSeat getShowSeat(Seat seat) {
        if (seat == null) {
            return null;
        }
        ShowSeat showSeat = seatIdToShowSeat.get(seat.getId());
        return showSeat != null && showSeat.getSeat() == seat ? showSeat : null;
    }

    /** Identity check prevents a ShowSeat belonging to another show being booked here. */
    public boolean ownsShowSeat(ShowSeat showSeat) {
        if (showSeat == null || !id.equals(showSeat.getShowId())) {
            return false;
        }
        return seatIdToShowSeat.get(showSeat.getSeatId()) == showSeat;
    }

    // getters & setters
    public String getId() { return id; }
    public Movie getMovie() { return movie; }
    public Screen getScreen() { return screen; }
    public PricingStrategy getPricingStrategy() { return pricingStrategy; }
    public Cinema getCinema() { return cinema; }
    public List<ShowSeat> getShowSeats() {return new ArrayList<>(seatIdToShowSeat.values());}
    public ShowSeat getShowSeatBySeatId(String seatId) { return seatIdToShowSeat.get(seatId);}
}
