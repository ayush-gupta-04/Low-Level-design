package MeetingScheduler.entities;

public class User {
    private final String id;
    private final String name;
    private final Schedule schedule;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
        this.schedule = new Schedule();
    }

    public String getName() { return name; }
    public String getId() { return id; }
    public Schedule getSchedule(){return this.schedule;}

}