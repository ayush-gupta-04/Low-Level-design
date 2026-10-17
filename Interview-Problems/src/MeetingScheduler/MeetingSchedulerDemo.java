package MeetingScheduler;

import java.time.LocalDateTime;
import java.util.*;

import MeetingScheduler.entities.Meeting;
import MeetingScheduler.entities.MeetingRoom;
import MeetingScheduler.entities.TimeSlot;
import MeetingScheduler.entities.User;
import MeetingScheduler.service.MeetingScheduler;
import MeetingScheduler.strategy.roomselection.LargestFitStrategy;
import MeetingScheduler.strategy.roomselection.SmallestFitStrategy;

/*
 * CORE REQUIREMENTS
 * 1. Add users and meeting rooms.
 * 2. Schedule a meeting: organizer, participants, time slot, room.
 * 3. A user or a room cannot be in two overlapping meetings.
 * 4. Auto-pick a free room that fits the participant count
 *    (the way a room is picked can be swapped: Strategy pattern).
 * 5. Cancel a meeting (frees up users and room).
 * 6. View a user's meetings.
 *
 * OPTIMISATIONS
 *  - Each calendar keeps meetings in a TreeMap sorted by start time -> O(log n) free-check.
 *  - Rooms are kept in a TreeMap sorted by capacity -> rooms that are too small
 *    are skipped in O(log R) instead of being scanned one by one.
 *
 * OUT OF SCOPE: UI, login, recurring meetings, time zones, real email.
 */



// ---------- 2. Calendar: TreeMap of start time -> meeting ----------
// A calendar never holds overlapping meetings, so meetings are sorted by start
// AND by end. To check a new slot we only look at its two neighbours.


public class MeetingSchedulerDemo {
    public static void main(String[] args) {
        MeetingScheduler scheduler = new MeetingScheduler();
        scheduler.setRoomSelectionStrategy(new SmallestFitStrategy());

        scheduler.addRoom(new MeetingRoom("R1", 2));
        scheduler.addRoom(new MeetingRoom("R2", 5));
        scheduler.addRoom(new MeetingRoom("R3", 10));

        User alice = new User("U1", "Alice");
        User bob = new User("U2", "Bob");
        User carol = new User("U3", "Carol");

        TimeSlot nineToTen = new TimeSlot(
                LocalDateTime.of(2026, 10, 15, 9, 0),
                LocalDateTime.of(2026, 10, 15, 10, 0));
        TimeSlot halfNine = new TimeSlot(
                LocalDateTime.of(2026, 10, 15, 9, 30),
                LocalDateTime.of(2026, 10, 15, 10, 30));
        TimeSlot elevenToTwelve = new TimeSlot(
                LocalDateTime.of(2026, 10, 15, 11, 0),
                LocalDateTime.of(2026, 10, 15, 12, 0));

        // 1. Alice + Bob -> smallest fit is R1
        Optional<Meeting> m1 = scheduler.scheduleMeeting("Standup", alice, List.of(bob), nineToTen);

        // 2. Bob is busy at 9:30 -> fails
        scheduler.scheduleMeeting("Design Review", carol, List.of(bob), halfNine);

        // 3. Carol alone at 9:30 -> R1 is taken, so R2
        scheduler.scheduleMeeting("1:1", carol, List.of(), halfNine);

        // 4. Only organizer can cancel
        scheduler.cancelMeeting(m1.get().getId(), bob);
        scheduler.cancelMeeting(m1.get().getId(), alice);

        // 5. After cancellation, Alice and Bob are free at 9:30 and R1 is free again
        scheduler.scheduleMeeting("Design Review", alice, List.of(bob), halfNine);

        // 6. Swap strategy at runtime: now the largest free room is preferred
        scheduler.setRoomSelectionStrategy(new LargestFitStrategy());
        scheduler.scheduleMeeting("All Hands", alice, List.of(bob, carol), elevenToTwelve);

        scheduler.printCalendarOfUser(bob);
        scheduler.printCalendarOfRoom(m1.get().getRoom());
    }
}