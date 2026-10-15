package MeetingScheduler.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import MeetingScheduler.registery.MeetingRegistry;
import MeetingScheduler.registery.RoomRegistry;
import MeetingScheduler.strategy.RoomSelectionStrategy;

public class MeetingScheduler {
    private final RoomRegistry roomRegistry = new RoomRegistry();
    private final MeetingRegistry meetingRegistry = new MeetingRegistry();
    private RoomSelectionStrategy roomStrategy;

    public MeetingScheduler(RoomSelectionStrategy roomStrategy) {
        this.roomStrategy = roomStrategy;
    }

    public void setRoomStrategy(RoomSelectionStrategy roomStrategy) {
        this.roomStrategy = roomStrategy;
    }

    public void addRoom(MeetingRoom room) {
        roomRegistry.addRoom(room);
    }

    public synchronized Meeting scheduleMeeting(String title, User organizer, List<User> invitees, TimeSlot slot) {
        List<User> attendees = new ArrayList<>(invitees);
        if (attendees.stream().noneMatch(u -> u.getId().equals(organizer.getId()))) {
            attendees.add(organizer);
        }

        for (User u : attendees) {
            if (!u.getSchedule().isFree(slot)) {
                System.out.println("Cannot schedule: " + u.getName() + " is busy.");
                return null;
            }
        }

        Optional<MeetingRoom> room = roomStrategy.selectRoom(roomRegistry.findAvailableRooms(slot), attendees.size());
        if (room.isEmpty()) {
            System.out.println("Cannot schedule: no free room available.");
            return null;
        }

        Meeting meeting = new Meeting(meetingRegistry.nextId(), title, organizer, attendees, room.get(), slot);

        for (User u : attendees) u.getSchedule().add(meeting);
        room.get().getSchedule().add(meeting);
        meetingRegistry.add(meeting);

        System.out.println("Scheduled: " + meeting);
        return meeting;
    }

    public synchronized boolean cancelMeeting(String meetingId, User requester) {
        Optional<Meeting> found = meetingRegistry.get(meetingId);
        if (found.isEmpty()) {
            System.out.println("Meeting not found.");
            return false;
        }

        Meeting meeting = found.get();
        if (!meeting.getOrganizer().getId().equals(requester.getId())) {
            System.out.println("Only the organizer can cancel.");
            return false;
        }

        for (User u : meeting.getParticipants()) u.getSchedule().remove(meeting);
        meeting.getRoom().getSchedule().remove(meeting);
        meetingRegistry.remove(meetingId);

        System.out.println("Cancelled: " + meetingId);
        return true;
    }

    public void printCalendar(User user) {
        System.out.println("--- Calendar of " + user.getName() + " ---");
        Collection<Meeting> all = user.getSchedule().getAll();
        if (all.isEmpty()) System.out.println("(no meetings)");
        for (Meeting m : all) System.out.println(m);
    }
}