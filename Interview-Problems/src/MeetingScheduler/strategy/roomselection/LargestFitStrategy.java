package MeetingScheduler.strategy.roomselection;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import MeetingScheduler.entities.MeetingRoom;
import MeetingScheduler.entities.TimeSlot;
import MeetingScheduler.strategy.RoomSelectionStrategy;

// Strategy B: largest room available (e.g. for comfort, or VIP meetings)
public class LargestFitStrategy implements RoomSelectionStrategy {
    @Override
    public Optional<MeetingRoom> selectRoom(List<MeetingRoom> rooms, TimeSlot slot) {
        Collections.reverse(rooms);
        for (MeetingRoom room : rooms) {
            if (room.getSchedule().isFree(slot)) return Optional.of(room);
        }
        return Optional.empty();
    }
}