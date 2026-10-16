package MeetingScheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import MeetingScheduler.entities.Meeting;
import MeetingScheduler.entities.MeetingRoom;
import MeetingScheduler.entities.TimeSlot;
import MeetingScheduler.entities.User;
import MeetingScheduler.service.RoomService;
import MeetingScheduler.service.UserService;
import MeetingScheduler.strategy.RoomSelectionStrategy;

public class MeetingScheduler {
    private final RoomService roomService;
    private final UserService userService;
    private final Map<String, Meeting> meetings;

    private RoomSelectionStrategy roomSelectionStrategy;


    public MeetingScheduler(RoomService roomService, UserService userService) {
        this.roomService = roomService;
        this.userService = userService;
        this.meetings = new HashMap<>();
    }

    public void setRoomSelectionStrategy(RoomSelectionStrategy roomSelectionStrategy){
        this.roomSelectionStrategy = roomSelectionStrategy;
    }
    // extra functions.
    public void addRoom(MeetingRoom room) {
        roomService.addRoom(room);
    }
    public void addUser(User user){
        userService.addUser(user);
    }
    // -----


    // schedule and cancel.
    public Optional<Meeting> scheduleMeeting(String title, User organizer, List<User> invitees, TimeSlot slot) {
        List<User> attendees = new ArrayList<>(invitees);
        attendees.add(organizer);
        for (User u : attendees) {
            if (!u.getSchedule().isFree(slot)) {
                System.out.println("Cannot schedule: " + u.getName() + " is busy.");
                return Optional.empty();
            }
        }
        Optional<MeetingRoom> room = roomSelectionStrategy.selectRoom(roomService.getAvailableMeetingRooms(slot), attendees.size());
        if (room.isEmpty()) {
            System.out.println("Cannot schedule: no free room available.");
            return Optional.empty();
        }

        Meeting meeting = new Meeting(title, organizer, attendees, room.get(), slot);

        for (User u : attendees) u.getSchedule().add(meeting);
        room.get().getSchedule().add(meeting);
        meetings.put(meeting.getId(), meeting);

        System.out.println("Scheduled: " + meeting);
        return Optional.of(meeting);
    }

    public boolean cancelMeeting(String meetingId, User requester) {
        Meeting meeting = meetings.get(meetingId);
        if (meeting == null) {
            System.out.println("Meeting not found.");
            return false;
        }
        if (!meeting.getOrganizer().getId().equals(requester.getId())) {
            System.out.println("Only the organizer can cancel.");
            return false;
        }
        for (User u : meeting.getParticipants()) u.getSchedule().remove(meeting);
        meeting.getRoom().getSchedule().remove(meeting);
        meetings.remove(meetingId);

        System.out.println("Cancelled: " + meetingId);
        return true;
    }

    public void printCalendarOfUser(User user) {
        System.out.println("--- Calendar of " + user.getName() + " ---");
        Collection<Meeting> all = user.getSchedule().getAll();
        if (all.isEmpty()) System.out.println("(no meetings)");
        for (Meeting m : all) System.out.println(m);
    }
    public void printCalendarOfRoom(MeetingRoom room) {
        System.out.println("--- Calendar of " + room.getId() + " ---");
        Collection<Meeting> all = room.getSchedule().getAll();
        if (all.isEmpty()) System.out.println("(no meetings)");
        for (Meeting m : all) System.out.println(m);
    }
}