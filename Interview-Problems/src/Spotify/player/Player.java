package Spotify.player;

import java.util.ArrayList;
import java.util.List;

import Spotify.entities.Playable;
import Spotify.entities.Song;
import Spotify.entities.User;

/**
 * One Player per user, so users never share playback state.
 * Public methods are synchronized so one user's player is safe across threads.
 * The state classes (same package) call the package-private helpers below.
 */
public class Player {
    private final User user;
    private PlayerState state;
    private List<Song> queue;
    private int currentIndex;
    private Song currentSong;
    private int posInSec;

    public Player(User user) { 
        this.user = user; 
        this.state = new StoppedState();
        this.queue = new ArrayList<>();
        this.currentIndex = 0;
        this.currentSong = null;
    }

    // ---- public controls: just delegate to the current state ----
    public synchronized void load(Playable playable) {
        queue = playable.getTracks();
        currentIndex = 0;
        currentSong = null;
        state = new StoppedState();
        System.out.printf("%s loaded %d track(s).%n", user.getName(), queue.size());
    }
    public synchronized void play() { state.play(this); }
    public synchronized void pause() { state.pause(this); }
    public synchronized void next() { state.next(this); }
    public synchronized void seek(int seconds) { state.seek(this, seconds); }


    
    // ---- helpers for the state classes ----
    void setState(PlayerState newState) { this.state = newState; }

    boolean hasSongs() { return !queue.isEmpty(); }

    void startCurrentSong() {
        currentSong = queue.get(currentIndex);
        posInSec = 0;
        if (user.getPlaybackStrategy().shouldPlayAd(user.getListeningHistory().size())) {
            System.out.println(">>> Ad: Buy Premium for ad-free music! <<<");
        }
        user.recordPlay(currentSong);
        System.out.printf("%s is now playing: %s%n", user.getName(), currentSong);
    }

    void playNextSong() {
        currentIndex = (currentIndex + 1) % queue.size();
        startCurrentSong();
    }

    void seekTo(int seconds) {
        if (seconds < 0 || seconds > currentSong.getDurationInSeconds()) {
            System.out.println("Invalid seek position.");
            return;
        }
        posInSec = seconds;
        System.out.printf("Seeked to %ds in %s%n", posInSec, currentSong);
    }
}
