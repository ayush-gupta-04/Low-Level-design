package FlightBooking.entities;

import java.util.UUID;

public class Passenger {
    private final String id;
    private final String name;
    private final int age;
    private final String contact;   // phone or email

    public Passenger(String name, int age, String contact) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.age = age;
        this.contact = contact;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getContact() { return contact; }
}
