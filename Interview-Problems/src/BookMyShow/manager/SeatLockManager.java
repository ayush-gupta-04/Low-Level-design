package manager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import entities.Seat;
import entities.Show;
import enums.SeatStatus;

public class SeatLockManager {
    private static SeatLockManager instance;
    private Map<Show, Map<Seat, String>> lockedSeats;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final long LOCK_TIMEOUT_MS = 10*1000; // 10 seconds. In real world, timeout would be in minutes

    private SeatLockManager(){
        this.lockedSeats = new ConcurrentHashMap<>();
    }
    public static SeatLockManager getInstance(){
        if(SeatLockManager.instance == null){
            synchronized (SeatLockManager.class){
                if(SeatLockManager.instance == null){
                    SeatLockManager.instance = new SeatLockManager();
                }
            }
        }
        return SeatLockManager.instance;
    }

    public void lockSeats(Show show, List<Seat> seats, String userId){
        synchronized(show){  // Synchronize on the show to ensure atomicity for that specific show
            // Check if any of the requested seats are already locked or booked
            for (Seat seat : seats) {
                if (seat.getStatus() != SeatStatus.AVAILABLE) {
                    System.out.println("Seat " + seat.getId() + " is not available.");
                    return;
                }
            }

            // lock the seats
            // - set seat status to LOCKED.
            // - save locked seats to lockedSeats map.
            for(Seat seat : seats){
                seat.setSeatStatus(SeatStatus.LOCKED);
            }
            if(!lockedSeats.containsKey(show)){
                lockedSeats.put(show , new ConcurrentHashMap<>());
            }
            for(Seat seat : seats){
                lockedSeats.get(show).put(seat, userId);
            }

            scheduler.schedule(() -> unlockSeats(show, seats, userId), LOCK_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            System.out.println("Locked seats: " + seats.stream().map(Seat::getId).toList() + " for user " + userId);
        }
    }
    public void unlockSeats(Show show, List<Seat> seats, String userId){
        synchronized(show){  // Synchronize on the show to ensure atomicity for that specific show
            Map<Seat, String> shows = lockedSeats.get(show);
            if(show != null){
                for(Seat seat : seats){
                    // only unlock if it is locked by the same user.
                    if(shows.containsKey(seat) && shows.get(seat).equals(userId)){
                        shows.remove(seat);
                        if(seat.getStatus() == SeatStatus.LOCKED){
                            seat.setSeatStatus(SeatStatus.AVAILABLE);
                            System.out.println("Unlocked seat: " + seat.getId() + " due to timeout.");
                        }else{
                            System.out.println("Unlocked seat: " + seat.getId() + " due to booking completion.");
                        }
                    }
                }
                if(shows.isEmpty()){
                    lockedSeats.remove(show);
                }
            }
        }
    }

    public void shutdown() {
        System.out.println("Shutting down SeatLockProvider scheduler.");
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
