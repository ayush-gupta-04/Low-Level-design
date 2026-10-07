package entities;

import java.util.List;
import java.util.UUID;

import enums.SeatStatus;

public class Booking {
    private String id;
    private User user;
    private Show show;
    private List<Seat> seats;
    private double amount;
    private Payment payment;

    public Booking(User user, Show show, List<Seat> seats, double amount, Payment payment){
        this.amount = amount;
        this.id = UUID.randomUUID().toString();
        this.payment = payment;
        this.seats = seats;
        this.show = show;
        this.user = user;
    } 

    public void confirmBooking(){
        for(Seat seat : seats){
            seat.setSeatStatus(SeatStatus.BOOKED);
        }
    }
    public String getId() { return id; }
    public User getUser() { return user; }
    public Show getShow() { return show; }
    public List<Seat> getSeats() { return seats; }
    public double getAmount() { return amount; }
    public Payment getPayment() { return payment; }
}
