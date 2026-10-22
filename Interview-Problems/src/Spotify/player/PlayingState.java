package Spotify.player;

class PlayingState implements PlayerState {
    @Override
    public void play(Player player) { System.out.println("Already playing."); }

    @Override
    public void pause(Player player) {
        System.out.println("Paused.");
        player.setState(new PausedState());
    }

    @Override
    public void next(Player player) { player.playNextSong(); }

    @Override
    public void seek(Player player, int seconds) { player.seekTo(seconds); }
}
