package Spotify.strategies.playback;

import Spotify.strategies.PlaybackStrategy;

public class FreePlaybackStrategy implements PlaybackStrategy {
    private static final int SONGS_BEFORE_AD = 3;

    @Override
    public boolean shouldPlayAd(int songsPlayedSoFar) {
        return songsPlayedSoFar > 0 && songsPlayedSoFar % SONGS_BEFORE_AD == 0;
    }
}
