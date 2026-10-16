package MeetingScheduler.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import MeetingScheduler.entities.MeetingRoom;
import MeetingScheduler.entities.TimeSlot;

// ---------- 6. RoomRegistry: rooms sorted by capacity ----------
// Optimization : 
// - we can have TreeMap<roomSize, List<MeetingRoom>>.
// - we can filter rooms having size >= people.
public class RoomService {
    private Map<String, MeetingRoom> rooms = new HashMap<>();

    public void addRoom(MeetingRoom room) {
        rooms.put(room.getId(), room);
    }
    public void removeRoom(String roomId){
        rooms.remove(roomId);
    }

    public List<MeetingRoom> getAvailableMeetingRooms(TimeSlot slot){
        List<MeetingRoom> list = new ArrayList<>();
        for(MeetingRoom r : rooms.values()){
            if(r.getSchedule().isFree(slot)) list.add(r);
        }
        return list;
    }
}