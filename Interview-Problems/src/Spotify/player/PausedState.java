package Spotify.player;

class PausedState implements PlayerState {
    @Override
    public void play(Player player) {
        System.out.println("Resuming.");
        player.setState(new PlayingState());
    }

    @Override
    public void pause(Player player) { System.out.println("Already paused."); }

    @Override
    public void next(Player player) {
        player.setState(new PlayingState());
        player.playNextSong();                    
    }

    @Override
    public void seek(Player player, int seconds) { player.seekTo(seconds); }
}
