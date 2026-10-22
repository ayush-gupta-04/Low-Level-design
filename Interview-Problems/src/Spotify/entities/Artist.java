package Spotify.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import Spotify.observer.ArtistObserver;

public class Artist {
    private final String id;
    private final String name;
    private final List<Album> albums;
    private final List<ArtistObserver> followers;

    public Artist(String name) { 
        this.name = name; 
        this.id = UUID.randomUUID().toString();
        this.albums = new ArrayList<>();
        this.followers = new ArrayList<>();
    }

    // Observer pattern
    public void addFollower(ArtistObserver follower) { followers.add(follower); }
    public void removeFollower(ArtistObserver follower) { followers.remove(follower); }
    public void releaseAlbum(Album album) {
        albums.add(album);
        followers.forEach(f -> f.update(this, album));
    }


    // getters
    public String getId() { return id; }
    public String getName() { return name; }
    public List<Album> getAlbums() { return List.copyOf(albums); }
}
