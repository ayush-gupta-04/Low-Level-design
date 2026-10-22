package Spotify.entities;

import java.util.ArrayList;
import java.util.List;

public class Playlist implements Playable {
    private final String name;
    private final List<Song> songs;

    public Playlist(String name) {
        this.name = name; 
        this.songs = new ArrayList<>();
    }

    public void addSong(Song song) { songs.add(song); }
    public void removeSong(Song song) { songs.remove(song); }

    @Override
    public List<Song> getTracks() { return List.copyOf(songs); }

    public String getName() { return name; }
}
