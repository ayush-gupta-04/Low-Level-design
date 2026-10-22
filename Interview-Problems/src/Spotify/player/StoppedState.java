package Spotify.player;

class StoppedState implements PlayerState {
    @Override
    public void play(Player player) {
        if (!player.hasSongs()) {
            System.out.println("Queue is empty. Load something first.");
            return;
        }
        player.setState(new PlayingState());
        player.startCurrentSong();
    }

    @Override
    public void pause(Player player) { System.out.println("Nothing is playing."); }

    @Override
    public void next(Player player) { System.out.println("Nothing is playing."); }

    @Override
    public void seek(Player player, int seconds) { System.out.println("Nothing is playing."); }
}
