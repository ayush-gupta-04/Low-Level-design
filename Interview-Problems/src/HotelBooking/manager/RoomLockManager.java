package HotelBooking.manager;

import java.lang.classfile.ClassFile.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import HotelBooking.entities.DateRange;
import HotelBooking.entities.Hotel;
import HotelBooking.entities.Room;

/*
    Locks are scoped by hotel and room because room IDs are only unique within a hotel.
    The outer map identifies the hotel; the inner map identifies a room within that hotel.
    Operations on a room's lock list are protected by synchronizing on that Room instance.
*/
public class RoomLockManager {

    private static class RoomLock {
        private final String id;
        private final String guestId;
        private final DateRange stay;

        private RoomLock(String id, String guestId, DateRange stay) {
            this.id = id;
            this.guestId = guestId;
            this.stay = stay;
        }
    }

    private static volatile RoomLockManager instance;

    private final Map<Hotel, Map<Room, Map<String, RoomLock>>> lockedRooms = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private volatile long lockTimeoutMs = 10 * 1000; // Demo timeout; production is usually longer.

    private RoomLockManager() { }

    public static RoomLockManager getInstance() {
        if (instance == null) {
            synchronized (RoomLockManager.class) {
                if (instance == null) {
                    instance = new RoomLockManager();
                }
            }
        }
        return instance;
    }

    /** Locks a room in the specified hotel for the dates, or returns empty if unavailable. */
    public Optional<String> lockRoom(Hotel hotel, Room room, DateRange stay, String guestId) {

        if (hotel==null || room==null || stay == null || guestId == null || guestId.isEmpty()) {
            return Optional.empty();
        }

        synchronized (room) {
            if(!room.isFree(stay)) return Optional.empty();

            for (RoomLock lock : getRoomLocks(hotel, room).values()) {
                if (lock.stay.overlaps(stay)) {
                    return Optional.empty();
                }
            }

            RoomLock lock = new RoomLock(UUID.randomUUID().toString(), guestId, stay);
            getRoomLocks(hotel, room).put(lock.id, lock);

            scheduler.schedule(() -> unlockRoom(hotel, room, lock.id), lockTimeoutMs, TimeUnit.MILLISECONDS);

            System.out.println("Locked room " + hotel.getId() + "/" + room.getId() + " for guest " + guestId);
            return Optional.of(lock.id);
        }
    }

    /** Confirms the room only if this guest still owns the lock for this hotel and room. */
    public boolean confirmRoom(Hotel hotel, Room room, String lockId, String guestId, String bookingId) {
        if (hotel==null || room == null || lockId==null || guestId == null || bookingId == null) {
            return false;
        }

        synchronized (room) {
            RoomLock lock = getRoomLocks(hotel, room).get(lockId);

            if (lock == null || !lock.guestId.equals(guestId)) {
                return false;
            }
            room.addStay(bookingId, lock.stay);
            getRoomLocks(hotel, room).remove(lockId);
            return true;
        }
    }

    /** Removes a temporary lock if it still exists. A confirmed room stays booked. */
    public void unlockRoom(Hotel hotel, Room room, String lockId) {
        if (hotel==null || room==null || lockId == null) {
            return;
        }

        synchronized (room) {
            RoomLock lock = getRoomLocks(hotel, room).get(lockId);
            if (lock != null) {
                getRoomLocks(hotel, room).remove(lockId);
                System.out.println("Unlocked room " + hotel.getId() + "/" + room.getId());
            }
        }
    }


    // ------ helper functions -------
    /** True if the dates are neither booked nor actively locked for this hotel and room. */
    public boolean isAvailable(Hotel hotel, Room room, DateRange stay) {
        if (hotel==null || room==null || stay==null) {
            return false;
        }

        synchronized (room) {
            if (!room.isFree(stay)) return false;
            for (Map.Entry<String, RoomLock> lock : getRoomLocks(hotel, room).entrySet()) {
                if (lock.getValue().stay.overlaps(stay)) {
                    return false;
                }
            }
            return true;
        }
    }

    // Only called while synchronized on the room. Concurrent inner maps allow different
    // rooms in the same hotel to be accessed safely at the same time.
    private Map<String, RoomLock> getRoomLocks(Hotel hotel, Room room) {
        return lockedRooms.computeIfAbsent(hotel, r -> new ConcurrentHashMap<>()).computeIfAbsent(room, r -> new ConcurrentHashMap<>());
    }

    public void shutdown() {
        System.out.println("Shutting down RoomLockManager scheduler.");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
