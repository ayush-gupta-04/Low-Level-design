package Spotify.strategies;

import java.util.List;

import Spotify.entities.Song;
import Spotify.entities.User;

public interface RecommendationStrategy {
    List<Song> recommend(User user, List<Song> allSongs);
}
