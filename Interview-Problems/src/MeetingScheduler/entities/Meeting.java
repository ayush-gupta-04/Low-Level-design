package MeetingScheduler.entities;

import java.util.List;
import java.util.UUID;

public class Meeting {
    private final String id;
    private final String title;
    private final User organizer;
    private final List<User> participants;
    private final MeetingRoom room;
    private final TimeSlot slot;

    public Meeting(String title, User organizer, List<User> participants, MeetingRoom room, TimeSlot slot) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.organizer = organizer;
        this.participants = List.copyOf(participants);
        this.room = room;
        this.slot = slot;
    }

    public String getId() { return id; }
    public User getOrganizer() { return organizer; }
    public List<User> getParticipants() { return participants; }
    public MeetingRoom getRoom() { return room; }
    public TimeSlot getSlot() { return slot; }

    @Override
    public String toString() {
        return "[" + id + "] " + title + " | Room " + room.getId() + " | " + slot;
    }
}
