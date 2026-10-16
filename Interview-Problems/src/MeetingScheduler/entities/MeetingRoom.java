package MeetingScheduler.entities;

public class MeetingRoom {
    private final String id;
    private final int capacity;
    private final Schedule schedule;

    public MeetingRoom(String id, int capacity) {
        this.id = id;
        this.capacity = capacity;
        this.schedule = new Schedule();        
    }

    // getters & setters
    public String getId() { return id; }
    public int getCapacity() { return capacity; }
    public Schedule getSchedule(){return this.schedule;}
}