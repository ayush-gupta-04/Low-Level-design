package MeetingScheduler.strategy.roomselection;

import java.util.List;
import java.util.Optional;

import MeetingScheduler.entities.MeetingRoom;
import MeetingScheduler.strategy.RoomSelectionStrategy;

// Strategy B: largest room available (e.g. for comfort, or VIP meetings)
public class LargestFitStrategy implements RoomSelectionStrategy {
    @Override
    public Optional<MeetingRoom> selectRoom(List<MeetingRoom> rooms, int size) {
        return rooms.stream()
        .filter(r -> r.getCapacity() >= size)
        .sorted((a,b) -> Integer.compare(b.getCapacity(), a.getCapacity()))
        .findFirst();
    }
}