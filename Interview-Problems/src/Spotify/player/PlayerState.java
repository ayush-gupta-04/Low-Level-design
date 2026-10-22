package Spotify.player;

interface PlayerState {
    void play(Player player);
    void pause(Player player);
    void next(Player player);
    void seek(Player player, int seconds);
}
