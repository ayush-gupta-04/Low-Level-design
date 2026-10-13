package HotelBooking.manager;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import HotelBooking.entities.Booking;
import HotelBooking.entities.DateRange;
import HotelBooking.entities.Guest;
import HotelBooking.entities.Hotel;
import HotelBooking.entities.Room;
import HotelBooking.service.HotelService;
import HotelBooking.strategies.PaymentStrategy;

public class HotelBookingSystem {
    private static HotelBookingSystem instance;
    private final Map<String, Guest> guests = new ConcurrentHashMap<>();
    private final BookingManager bookingMgr;
    private final HotelService hotelService;
    private final RoomLockManager roomLockMgr;

    private HotelBookingSystem() {
        this.bookingMgr = BookingManager.getInstance();
        this.hotelService = new HotelService();
        this.roomLockMgr = RoomLockManager.getInstance();
    }

    public static synchronized HotelBookingSystem getInstance() {
        if (instance == null) {
            instance = new HotelBookingSystem();
        }
        return instance;
    }

    
    // main methods.
    public Optional<Booking> bookRoom(String guestId, String hotelId, String roomId, LocalDate checkIn, LocalDate checkOut, PaymentStrategy paymentStrategy) {

        Hotel hotel = hotelService.getHotel(hotelId);
        Room room = hotel == null ? null : hotel.getRoom(roomId);
        DateRange dateRange = new DateRange(checkIn, checkOut);

        if(hotel==null || room == null) return Optional.empty();

        return bookingMgr.createBooking(guests.get(guestId), hotel, room, dateRange, paymentStrategy);
    }

    public boolean cancelBooking(String guestId, String bookingId) {
        return bookingMgr.cancelBooking(guestId, bookingId);
    }

  



    // helper functions
    public Hotel addHotel(String id, String name, String address, String city) {
        return hotelService.addHotel(new Hotel(id, name, address, city));
    }

    public Room addRoom(String hotelId, Room room) {
        return hotelService.getHotel(hotelId).addRoom(room);
    }

    public Guest addGuest(String name, String email) {
        Guest guest = new Guest(name, email);
        guests.putIfAbsent(guest.getId(), guest);
        return guest;
    }
    public boolean isRoomAvailable(String hotelId, String roomId, LocalDate checkIn, LocalDate checkOut) {
        Hotel hotel = hotelService.getHotel(hotelId);
        Room room = hotel == null ? null : hotel.getRoom(roomId);
        return room != null && roomLockMgr.isAvailable(hotel, room, new DateRange(checkIn, checkOut));
    }


    // search
    public List<Hotel> searchHotelsByCity(String city) {
        return hotelService.searchHotelsByCity(city);
    }
    public List<Booking> getBookingsForGuest(String guestId) {
        return bookingMgr.getBookingsForGuest(guestId);
    }


    // shutdown
    public void shutdown() {
        bookingMgr.shutdown();
    }

}
