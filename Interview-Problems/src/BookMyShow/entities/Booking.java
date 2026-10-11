package entities;

import java.util.List;
import java.util.UUID;

public class Booking {
    private final String id;
    private final User user;
    private final Show show;
    private final List<ShowSeat> showSeats;
    private final double amount;
    private final Payment payment;

    public Booking(User user, Show show, List<ShowSeat> showSeats, double amount, Payment payment) {
        this.amount = amount;
        this.id = UUID.randomUUID().toString();
        this.payment = payment;
        this.showSeats = List.copyOf(showSeats);   // because the showSeats can change .. so copy them.
        this.show = show;
        this.user = user;
    }

    public String getId() { return id; }
    public User getUser() { return user; }
    public Show getShow() { return show; }
    public List<ShowSeat> getShowSeats() { return showSeats; }
    public double getAmount() { return amount; }
    public Payment getPayment() { return payment; }
}
