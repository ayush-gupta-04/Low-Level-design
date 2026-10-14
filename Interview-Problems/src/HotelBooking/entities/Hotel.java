package HotelBooking.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hotel {
    private final String id;
    private final String name;
    private final String address;
    private final String city;
    // Room IDs are unique only within this hotel.
    private final Map<String, Room> rooms = new HashMap<>();

    public Hotel(String id, String name, String address, String city) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
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
}
