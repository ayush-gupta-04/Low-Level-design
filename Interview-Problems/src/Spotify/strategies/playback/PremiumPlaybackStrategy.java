package Spotify.strategies.playback;

import Spotify.strategies.PlaybackStrategy;

public class PremiumPlaybackStrategy implements PlaybackStrategy {
    @Override
    public boolean shouldPlayAd(int songsPlayedSoFar) { return false; }
}
