package Spotify.entities;

import java.util.List;

// Anything the player can load: a song, an album or a playlist.
public interface Playable {
    List<Song> getTracks();
}
