package MeetingScheduler.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import MeetingScheduler.strategy.RoomSelectionStrategy;

// ---------- 8. MeetingScheduler: the main service ----------
public class MeetingScheduler {
    private final RoomRegistry roomRegistry = new RoomRegistry();
    private final Map<String, Meeting> meetings = new HashMap<>();
    private RoomSelectionStrategy roomStrategy;
    private int meetingCounter = 0;

    public MeetingScheduler(RoomSelectionStrategy roomStrategy) {
        this.roomStrategy = roomStrategy;
    }

    // Strategy can be swapped at runtime
    public void setRoomStrategy(RoomSelectionStrategy roomStrategy) {
        this.roomStrategy = roomStrategy;
    }
    public void addRoom(MeetingRoom room) {
        roomRegistry.addRoom(room);
    }


    // -------- Main Methods -----------
    // synchronized so two threads cannot book the same slot at once
    public synchronized Meeting scheduleMeeting(String title, User organizer, List<User> invitees, TimeSlot slot) {
        // Everyone attending = organizer + invitees
        List<User> attendees = new ArrayList<>(invitees);
        if (!attendees.contains(organizer)) {
            attendees.add(organizer);
        }

        // Step 1: every attendee must be free
        for (User u : attendees) {
            if (!u.getSchedule().isFree(slot)) {
                System.out.println("Cannot schedule: " + u.getName() + " is busy.");
                return null;
            }
        }

        // Step 2: ask the strategy for a room (only rooms that fit are passed in)
        Optional<MeetingRoom> room = roomStrategy.selectRoom(roomRegistry.roomsFitting(attendees.size()), slot);
        if (room.isEmpty()) {
            System.out.println("Cannot schedule: no free room available.");
            return null;
        }

        // Step 3: create meeting and block calendars
        String id = "M" + (++meetingCounter);
        Meeting meeting = new Meeting(id, title, organizer, attendees, room.get(), slot);

        for (User u : attendees) u.getSchedule().add(meeting);
        room.get().getSchedule().add(meeting);
        meetings.put(id, meeting);

        System.out.println("Scheduled: " + meeting);
        return meeting;
    }

    // Only the organizer can cancel
    public synchronized boolean cancelMeeting(String meetingId, User requester) {
        Meeting meeting = meetings.get(meetingId);
        if (meeting == null) {
            System.out.println("Meeting not found.");
            return false;
        }
        if (meeting.getOrganizer() != requester) {
            System.out.println("Only the organizer can cancel.");
            return false;
        }

        for (User u : meeting.getParticipants()) u.getSchedule().remove(meeting);
        meeting.getRoom().getSchedule().remove(meeting);
        meetings.remove(meetingId);

        System.out.println("Cancelled: " + meetingId);
        return true;
    }
    // ------ End of Main Methods ------

    public void printCalendar(User user) {
        System.out.println("--- Calendar of " + user.getName() + " ---");
        Collection<Meeting> all = user.getSchedule().getAll();
        if (all.isEmpty()) System.out.println("(no meetings)");
        for (Meeting m : all) System.out.println(m);
    }
}
