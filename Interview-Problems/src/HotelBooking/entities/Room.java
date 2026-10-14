package HotelBooking.entities;

import java.util.HashMap;
import java.util.Map;

import HotelBooking.enums.RoomType; 


public abstract class Room {
    private final String id;
    private final String roomNumber;
    private final RoomType type;
    private final double pricePerNight;
    private final int capacity;
    private final Map<String, DateRange> confirmedStays = new HashMap<>();  // bookingId -> DateRange

    protected Room(String id, String roomNumber, RoomType type, double pricePerNight, int capacity) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
    }

    // ------ main methods ------
    // must synchronize on this room; 
    // RoomLockManager does that for the system.
    // Optimisation : 
    // - we could use TreeMap, find floor and ceil key and check overlap.
    public boolean isFree(DateRange stay) {
        for (DateRange booked : confirmedStays.values()) {
            if (booked.overlaps(stay)) {
                return false;
            }
        }
        return true;
    }
    public void addStay(String bookingId, DateRange stay) {
        confirmedStays.put(bookingId, stay);
    }
    public boolean removeStay(String bookingId) {
        return confirmedStays.remove(bookingId) != null;
    }


    // ------ Getters & Setters ---------
    public String getId() { return id; }
    public String getRoomNumber() { return roomNumber; }
    public RoomType getType() { return type; }
    public double getPricePerNight() { return pricePerNight; }
    public int getCapacity() { return capacity; }

}
