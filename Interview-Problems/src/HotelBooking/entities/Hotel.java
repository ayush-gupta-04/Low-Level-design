package HotelBooking.entities;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Hotel {
    private final String id;
    private final String name;
    private final String address;
    private final String city;
    // Room IDs are unique only within this hotel.
    private final Map<String, Room> rooms = new LinkedHashMap<>();

    public Hotel(String id, String name, String address, String city) {
        this.id = requireText(id, "hotel id");
        this.name = requireText(name, "hotel name");
        this.address = requireText(address, "hotel address");
        this.city = requireText(city, "hotel city");
    }

    public Room addRoom(Room room) {
        return rooms.put(room.getId(), room);
    }

    public synchronized Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    /** Verifies that this exact room instance is registered with this hotel. */
    public synchronized boolean hasRoom(Room room) {
        return room != null && rooms.get(room.getId()) == room;
    }

    public synchronized List<Room> getRooms() {
        return List.copyOf(new ArrayList<>(rooms.values()));
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getCity() { return city; }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value;
    }
}
