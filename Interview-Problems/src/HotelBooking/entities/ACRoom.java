package HotelBooking.entities;

import HotelBooking.enums.RoomType;

public class ACRoom extends Room {
    public ACRoom(String id, String roomNumber, RoomType type, double pricePerNight, int capacity) {
        super(id, roomNumber, type, pricePerNight, capacity);
    }
}
