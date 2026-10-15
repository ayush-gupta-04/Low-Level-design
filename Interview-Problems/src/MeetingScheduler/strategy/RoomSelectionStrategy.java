package MeetingScheduler.strategy;

import java.util.List;
import java.util.Optional;

import MeetingScheduler.entities.MeetingRoom;

public interface RoomSelectionStrategy {
    // fittingRooms: only rooms big enough, sorted by capacity (small -> large)
    // Returns a free room for the slot, or null if none.
    Optional<MeetingRoom> selectRoom(List<MeetingRoom> rooms, int size);
}
