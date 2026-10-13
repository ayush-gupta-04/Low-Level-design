package HotelBooking.entities;

import HotelBooking.enums.BookingStatus;

public class Booking {
    private final String id;
    private final Guest guest;
    private final Hotel hotel;
    private final Room room;
    private final DateRange stay;
    private final double amount;
    private final Payment payment;
    private BookingStatus status;

    // A booking is only created after payment and room confirmation succeed.
    public Booking(String id, Guest guest, Hotel hotel, Room room, DateRange stay, double amount, Payment payment) {
        this.id = id;
        this.guest = guest;
        this.hotel = hotel;
        this.room = room;
        this.stay = stay;
        this.amount = amount;
        this.payment = payment;
        this.status = BookingStatus.CONFIRMED;
    }

    public void markCancelled() { this.status = BookingStatus.CANCELLED; }

    public String getId() { return id; }
    public Guest getGuest() { return guest; }
    public Hotel getHotel() { return hotel; }
    public Room getRoom() { return room; }
    public DateRange getStay() { return stay; }
    public double getAmount() { return amount; }
    public Payment getPayment() { return payment; }
    public BookingStatus getStatus() { return status; }
}
