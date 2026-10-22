package MeetingScheduler.service;

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
import MeetingScheduler.enums.MeetingStatus;
import MeetingScheduler.strategy.RoomSelectionStrategy;



// TODO :
// 1. Resource Level Locking : 
//    - we have lock on the whole meeting scheduler.
//    - not a good design
//    - we must have lock on the (rooms + users)


public class MeetingScheduler {
    private Map<String, MeetingRoom> rooms;
    private Map<String, User> users;
    private final Map<String, Meeting> meetings;

    private RoomSelectionStrategy roomSelectionStrategy;

    public MeetingScheduler(RoomSelectionStrategy roomSelectionStrategy) {
        this.rooms = new HashMap<>();
        this.users = new HashMap<>();
        this.meetings = new HashMap<>();
        this.roomSelectionStrategy = roomSelectionStrategy;
    }

    public void setRoomSelectionStrategy(RoomSelectionStrategy roomSelectionStrategy){
        this.roomSelectionStrategy = roomSelectionStrategy;
    }
    // extra functions.
    public void addRoom(MeetingRoom room) {
        rooms.put(room.getId(), room);
    }
    public void addUser(User user){
        users.put(user.getId(), user);
    }
    // -----


    // schedule and cancel.
    public synchronized Optional<Meeting> scheduleMeeting(String title, User organizer, List<User> invitees, TimeSlot slot) {
        // make a copy list & add organizer to it.
        List<User> attendees = new ArrayList<>(invitees);
        attendees.add(organizer);

        for (User u : attendees) {
            if (!u.getSchedule().isFree(slot)) {
                System.out.println("Cannot schedule: " + u.getName() + " is busy.");
                return Optional.empty();
            }
        }
        // find available rooms
        List<MeetingRoom> listOfMeetingRooms = new ArrayList<>();
        for(MeetingRoom r : rooms.values()){
            if(r.getSchedule().isFree(slot) && r.getCapacity() >= attendees.size()) listOfMeetingRooms.add(r);
        }

        // select from available rooms.
        Optional<MeetingRoom> room = roomSelectionStrategy.selectRoom(listOfMeetingRooms, attendees.size());
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

    public synchronized boolean cancelMeeting(String meetingId, User requester) {
        Meeting meeting = meetings.get(meetingId);

        if (meeting == null 
            || meeting.getMeetingStatus()==MeetingStatus.CANCELLED
            || !meeting.getOrganizer().getId().equals(requester.getId())) {

            return false;
        }


        // remove meeting from user & room.
        // keep the meeting in meetings[] (just mark it CANCELLED)
        for (User u : meeting.getParticipants()) u.getSchedule().remove(meeting);
        meeting.getRoom().getSchedule().remove(meeting);
        meeting.setMeetingStatus(MeetingStatus.CANCELLED);

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