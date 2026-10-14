package MeetingScheduler.entities;

public class MeetingRoom {
    private final String id;
    private final int capacity;
    private final Schedule schedule = new Schedule();

    public MeetingRoom(String id, int capacity) {
        this.id = id;
        this.capacity = capacity;
    }

    // getters & setters
    public String getId() { return id; }
    public int getCapacity() { return capacity; }
    public Schedule getSchedule() { return schedule; }
}