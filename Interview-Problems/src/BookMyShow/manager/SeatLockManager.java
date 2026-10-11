package manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import entities.Show;
import entities.ShowSeat;
import enums.SeatStatus;

public class SeatLockManager {
    private static volatile SeatLockManager instance;

    // ShowSeat instances are show-specific, so locks cannot leak across shows.
    private final Map<Show, Map<ShowSeat, String>> lockedSeats = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final long LOCK_TIMEOUT_MS = 10 * 1000; // Demo timeout; production is usually longer.

    private SeatLockManager() { }

    public static SeatLockManager getInstance() {
        if (instance == null) {
            synchronized (SeatLockManager.class) {
                if (instance == null) {
                    instance = new SeatLockManager();
                }
            }
        }
        return instance;
    }

    /** Atomically locks every requested ShowSeat, or locks none of them. */
    public boolean lockSeats(Show show, List<ShowSeat> seats, String userId) {

        if (show == null || seats == null || seats.isEmpty() || userId == null || userId.isEmpty()) {
            return false;
        }
        List<ShowSeat> requestedSeats = List.copyOf(seats);

        // Synchronize on the show to ensure atomicity for that specific show
        synchronized (show) {
            Set<ShowSeat> uniqueSeats = new HashSet<>();
            Map<ShowSeat, String> showLocks = lockedSeats.get(show);

            // Validate the entire request before changing any seat's state.
            for (ShowSeat showSeat : requestedSeats) {
                if (!show.ownsShowSeat(showSeat) || !uniqueSeats.add(showSeat)) {
                    return false;
                }
                if (showSeat.getStatus() != SeatStatus.AVAILABLE || (showLocks != null && showLocks.containsKey(showSeat))) {
                    System.out.println("Seat " + showSeat.getSeatId() + " is not available.");
                    return false;
                }
            }

            if (showLocks == null) {
                showLocks = new HashMap<>();
                lockedSeats.put(show, showLocks);
            }

            for (ShowSeat showSeat : requestedSeats) {
                showSeat.setStatus(SeatStatus.LOCKED);
                showLocks.put(showSeat, userId);
            }

            // Use a copied list so later caller-side list changes cannot affect timeout cleanup.
            scheduler.schedule(
                () -> unlockSeats(show, requestedSeats, userId),
                LOCK_TIMEOUT_MS,
                TimeUnit.MILLISECONDS
            );
            System.out.println("Locked seats: "
                + requestedSeats.stream().map(ShowSeat::getSeatId).toList()
                + " for user " + userId + " in show " + show.getId());
            return true;
        }
    }

    /** Confirms seats only if every requested lock is still owned by this user. */
    public boolean confirmSeats(Show show, List<ShowSeat> seats, String userId) {
        if (show == null || seats == null || seats.isEmpty() || userId == null) {
            return false;
        }

        synchronized (show) {
            Map<ShowSeat, String> showLocks = lockedSeats.get(show);
            if (showLocks == null) {
                return false;
            }

            Set<ShowSeat> uniqueSeats = new HashSet<>();
            for (ShowSeat showSeat : seats) {
                if (!show.ownsShowSeat(showSeat)
                        || !uniqueSeats.add(showSeat)
                        || !userId.equals(showLocks.get(showSeat))
                        || showSeat.getStatus() != SeatStatus.LOCKED) {
                    return false;
                }
            }

            // All locks passed validation; now atomically commit the whole set.
            for (ShowSeat showSeat : seats) {
                showSeat.setStatus(SeatStatus.BOOKED);
                showLocks.remove(showSeat);
            }
            if (showLocks.isEmpty()) {
                lockedSeats.remove(show, showLocks);
            }
            return true;
        }
    }

    /** Releases only locks currently owned by userId; booked seats stay booked. */
    public void unlockSeats(Show show, List<ShowSeat> seats, String userId) {
        if (show == null || seats == null || userId == null) {
            return;
        }

        synchronized (show) {
            Map<ShowSeat, String> showLocks = lockedSeats.get(show);
            if (showLocks == null) {
                return;
            }

            for (ShowSeat showSeat : seats) {
                if (showSeat != null && userId.equals(showLocks.get(showSeat))) {
                    showLocks.remove(showSeat);
                    if (showSeat.getStatus() == SeatStatus.LOCKED) {
                        showSeat.setStatus(SeatStatus.AVAILABLE);
                        System.out.println("Unlocked seat: " + showSeat.getSeatId()
                            + " for show " + show.getId());
                    }
                }
            }
            if (showLocks.isEmpty()) {
                lockedSeats.remove(show, showLocks);
            }
        }
    }

    public void shutdown() {
        System.out.println("Shutting down SeatLockManager scheduler.");
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
