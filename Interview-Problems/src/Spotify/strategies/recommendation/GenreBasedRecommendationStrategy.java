package Spotify.strategies.recommendation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Spotify.entities.Song;
import Spotify.entities.User;
import Spotify.enums.Genre;
import Spotify.strategies.RecommendationStrategy;

// Filter out the Genre the user listened the Most.

public class GenreBasedRecommendationStrategy implements RecommendationStrategy {
    private static final int LIMIT = 5;

    @Override
    public List<Song> recommend(User user, List<Song> allSongs) {
        List<Song> history = user.getListeningHistory();

        // Find out the Genre Frequency.
        Map<Genre, Integer> genreFreq = new HashMap<>();
        for(Song s : history){
            genreFreq.put(s.getGenre(), genreFreq.getOrDefault(s.getGenre(), 0) + 1);
        }

        // New user with no history: nothing to personalise on, so show a few songs.
        if (genreFreq.isEmpty()) {
            return allSongs.stream().limit(LIMIT).toList();
        }

        // Extract the Most Listened genre
        Genre favourite = null;
        int maxFreq = 0;
        for(Map.Entry<Genre, Integer> e : genreFreq.entrySet()){
            if(e.getValue() > maxFreq){
                favourite = e.getKey();
                maxFreq = e.getValue();
            }
        }

        Genre favouriteGenre = favourite;

        return allSongs.stream()
                .filter(song -> song.getGenre() == favouriteGenre && !history.contains(song))
                .limit(LIMIT)
                .toList();
    }
}
