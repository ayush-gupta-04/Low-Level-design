package Spotify.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import Spotify.observer.ArtistObserver;
import Spotify.strategies.PlaybackStrategy;

public class User implements ArtistObserver {
    private final String id;
    private final String name;
    private final List<Song> listeningHistory;
    private final List<Artist> followedArtists;
    private final List<Playlist> playlists;
    private PlaybackStrategy playbackStrategy;

    public User(String name, PlaybackStrategy playbackStrategy) {
        this.id =  UUID.randomUUID().toString();
        this.name = name;
        this.playbackStrategy = playbackStrategy;
        this.listeningHistory = new ArrayList<>();
        this.followedArtists = new ArrayList<>();
        this.playlists = new ArrayList<>();
    }

    // For Following Artist
    public void follow(Artist artist) {
        if (followedArtists.add(artist)) {
            artist.addFollower(this);
        }
    }
    public void unfollow(Artist artist) {
        if (followedArtists.remove(artist)) {
            artist.removeFollower(this);
        }
    }

    // Playlist Methods
    public Playlist createPlaylist(String playlistName) {
        Playlist playlist = new Playlist(playlistName);
        playlists.add(playlist);
        return playlist;
    }


    // Songs History Methods
    public void recordPlay(Song song) { listeningHistory.add(song); }


    // Observer implementation
    @Override
    public void update(Artist artist, Album album) {
        System.out.printf("[Notification for %s] %s just released: %s!%n",name, artist.getName(), album.getTitle());
    }

    // getters & helpers
    public void setPlaybackStrategy(PlaybackStrategy strategy) { this.playbackStrategy = strategy; }
    public PlaybackStrategy getPlaybackStrategy() { return playbackStrategy; }
    public List<Song> getListeningHistory() { return List.copyOf(listeningHistory); }
    public List<Playlist> getPlaylists() { return List.copyOf(playlists); }
    public String getId() { return id; }
    public String getName() { return name; }
}
