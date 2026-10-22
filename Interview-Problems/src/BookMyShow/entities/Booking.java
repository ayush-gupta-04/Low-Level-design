package BookMyShow.entities;

import java.util.List;

import BookMyShow.enums.BookingStatus;

public class Booking {
    private final String id;
    private final User user;
    private final Show show;
    private final List<ShowSeat> showSeats;
    private final double amount;
    private BookingStatus status;

    public Booking(String id, User user, Show show, List<ShowSeat> showSeats, double amount) {
        this.amount = amount;
        this.id = id;
        this.showSeats = List.copyOf(showSeats);   // because the showSeats can change .. so copy them.
        this.show = show;
        this.user = user;
        this.status = BookingStatus.CONFIRMED;
    }

    public String getId() { return id; }
    public User getUser() { return user; }
    public Show getShow() { return show; }
    public List<ShowSeat> getShowSeats() { return showSeats; }
    public double getAmount() { return amount; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) {this.status = status;}
}
