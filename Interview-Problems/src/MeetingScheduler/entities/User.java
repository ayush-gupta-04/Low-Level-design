package MeetingScheduler.entities;

public class User {
    private final String id;

    private final String name;
    private final Schedule schedule = new Schedule();

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() { return name; }
    public Schedule getSchedule() { return schedule; }
    public String getId() { return id; }
}