package MeetingScheduler.entities;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class Schedule {
    private final TreeMap<LocalDateTime, Meeting> meetings = new TreeMap<>();  // startTime -> Meeting

    // O(log n)
    public boolean isFree(TimeSlot slot) {
        // Meeting starting at or before the new start: does it run into the new slot?
        Map.Entry<LocalDateTime, Meeting> before = meetings.floorEntry(slot.getStart());
        if (before != null && before.getValue().getSlot().getEnd().isAfter(slot.getStart())) {
            return false;
        }

        // Meeting starting at or after the new start: does it begin inside the new slot?
        Map.Entry<LocalDateTime, Meeting> after = meetings.ceilingEntry(slot.getStart());
        if (after != null && after.getKey().isBefore(slot.getEnd())) {
            return false;
        }

        return true;
    }

    public void add(Meeting m) {
        meetings.put(m.getSlot().getStart(), m);
    }

    public void remove(Meeting m) {
        meetings.remove(m.getSlot().getStart());
    }

    public Collection<Meeting> getAll() {
        return meetings.values();
    }
}