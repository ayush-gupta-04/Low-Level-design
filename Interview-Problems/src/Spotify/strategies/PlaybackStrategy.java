package Spotify.strategies;

/** Decides how a subscription tier behaves. It only decides - the Player does the playing. */
public interface PlaybackStrategy {
    boolean shouldPlayAd(int songsPlayedSoFar);
}
