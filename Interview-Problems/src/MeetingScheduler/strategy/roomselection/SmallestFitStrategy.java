package MeetingScheduler.strategy.roomselection;

import java.util.List;
import java.util.Optional;

import MeetingScheduler.entities.MeetingRoom;
import MeetingScheduler.entities.TimeSlot;
import MeetingScheduler.strategy.RoomSelectionStrategy;

// Strategy A: smallest room that fits (saves big rooms for big meetings)
public class SmallestFitStrategy implements RoomSelectionStrategy {
    @Override
    public Optional<MeetingRoom> selectRoom(List<MeetingRoom> rooms, TimeSlot slot) {
        for (MeetingRoom room : rooms) {
            if (room.getSchedule().isFree(slot)) return Optional.of(room);
        }
        return Optional.empty();
    }
}