package Spotify.entities;

import java.util.List;
import java.util.UUID;

import Spotify.enums.Genre;

public class Song implements Playable {
    private final String id;
    private final String title;
    private final Artist artist;
    private final Genre genre;
    private final int durationInSeconds;

    public Song(String title, Artist artist, Genre genre, int durationInSeconds) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.durationInSeconds = durationInSeconds;
    }

    @Override
    public List<Song> getTracks() { return List.of(this); }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public Artist getArtist() { return artist; }
    public Genre getGenre() { return genre; }
    public int getDurationInSeconds() { return durationInSeconds; }

    @Override
    public String toString() { return "'" + title + "' by " + artist.getName(); }
}
