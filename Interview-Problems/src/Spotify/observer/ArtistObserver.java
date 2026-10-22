package Spotify.observer;

import Spotify.entities.Album;
import Spotify.entities.Artist;

/** Implemented by anyone who wants to hear about an artist's new releases. */
public interface ArtistObserver {
    void update(Artist artist, Album album);
}
