package MeetingScheduler.strategy.roomselection;

import java.util.List;
import java.util.Optional;

import MeetingScheduler.entities.MeetingRoom;
import MeetingScheduler.strategy.RoomSelectionStrategy;

// Strategy A: smallest room that fits (saves big rooms for big meetings)
public class SmallestFitStrategy implements RoomSelectionStrategy {
    @Override
    public Optional<MeetingRoom> selectRoom(List<MeetingRoom> rooms, int size) {
        return rooms.stream()
        .filter(r -> r.getCapacity() >= size)
        .sorted((a,b) -> Integer.compare(a.getCapacity(), b.getCapacity()))
        .findFirst();
    }
}