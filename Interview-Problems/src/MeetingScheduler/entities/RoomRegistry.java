package MeetingScheduler.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// ---------- 6. RoomRegistry: rooms sorted by capacity ----------
// Optimization : 
// - we can have TreeMap<roomSize, List<MeetingRoom>>.
// - we can filter rooms having size >= people.
public class RoomRegistry {
    private List<MeetingRoom> rooms = new ArrayList<>();

    public void addRoom(MeetingRoom room) {
        rooms.add(room);
    }

    // All rooms with capacity >= people, sorted by capacity.
    public List<MeetingRoom> roomsFitting(int people) {
        return rooms.stream()
        .filter(r -> r.getCapacity() >= people)
        .sorted((a,b) -> Integer.compare(a.getCapacity(), b.getCapacity()))
        .collect(Collectors.toCollection(ArrayList::new));
    }
}