package Spotify.entities;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import Spotify.enums.Genre;

/** Stores songs, albums and artists, and answers search queries. */
public class MusicCatalog {
    private final Map<String, Song> songs;
    private final Map<String, Album> albums;
    private final Map<String, Artist> artists;

    public MusicCatalog(){
        this.songs = new ConcurrentHashMap<>();
        this.albums = new ConcurrentHashMap<>();
        this.artists = new ConcurrentHashMap<>();
    }

    public Artist addArtist(String name) {
        Artist artist = new Artist(name);
        artists.put(artist.getId(), artist);
        return artist;
    }
    public Song addSong(String title, Artist artist, Genre genre, int durationInSeconds) {
        Song song = new Song(title, artist, genre, durationInSeconds);
        songs.put(song.getId(), song);
        return song;
    }
    public Album addAlbum(String title, Artist artist) {
        Album album = new Album(title, artist);
        albums.put(album.getId(), album);
        return album;
    }

    public List<Song> getAllSongs() { return List.copyOf(songs.values()); }

    public List<Song> searchSongs(String query) {
        return songs.values().stream().filter(s -> matches(s.getTitle(), query)).toList();
    }
    public List<Album> searchAlbums(String query) {
        return albums.values().stream().filter(a -> matches(a.getTitle(), query)).toList();
    }
    public List<Artist> searchArtists(String query) {
        return artists.values().stream().filter(a -> matches(a.getName(), query)).toList();
    }

    private boolean matches(String text, String query) {
        return text.toLowerCase().contains(query.toLowerCase());
    }
}
