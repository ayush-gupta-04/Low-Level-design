package HotelBooking.entities;

import HotelBooking.enums.RoomType;

public class NonACRoom extends Room {
    public NonACRoom(String id, String roomNumber, RoomType type, double pricePerNight, int capacity) {
        super(id, roomNumber, type, pricePerNight, capacity);
    }
}
