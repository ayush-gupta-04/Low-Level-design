package HotelBooking.entities;

import java.util.UUID;

public class Guest {
    private final String id;
    private final String name;
    private final String email;

    public Guest(String name, String email) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
