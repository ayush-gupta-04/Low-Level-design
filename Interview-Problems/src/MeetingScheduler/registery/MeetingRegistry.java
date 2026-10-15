package MeetingScheduler.registery;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import MeetingScheduler.entities.Meeting;

public class MeetingRegistry {
    private final Map<String, Meeting> meetings = new HashMap<>();
    private int counter = 0;

    public String nextId() {
        return "M" + (++counter);
    }

    public void add(Meeting meeting) {
        meetings.put(meeting.getId(), meeting);
    }

    public Optional<Meeting> get(String meetingId) {
        return Optional.ofNullable(meetings.get(meetingId));
    }

    public void remove(String meetingId) {
        meetings.remove(meetingId);
    }

    public Collection<Meeting> getAll() {
        return meetings.values();
    }
}