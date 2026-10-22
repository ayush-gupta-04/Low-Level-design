package Spotify;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import Spotify.entities.MusicCatalog;
import Spotify.entities.Song;
import Spotify.entities.User;
import Spotify.player.Player;
import Spotify.strategies.PlaybackStrategy;
import Spotify.strategies.RecommendationStrategy;
import Spotify.strategies.recommendation.GenreBasedRecommendationStrategy;

public class MusicStreamingSystem {
    private static MusicStreamingSystem INSTANCE;
    private final MusicCatalog catalog;
    private final Map<String, User> users;       // userId -> User
    private final Map<String, Player> players;   // userId -> Player
    private volatile RecommendationStrategy recommendationStrategy;

    private MusicStreamingSystem(RecommendationStrategy recommendationStrategy) {
        this.catalog = new MusicCatalog();
        this.users = new ConcurrentHashMap<>();
        this.players = new ConcurrentHashMap<>();
        this.recommendationStrategy = recommendationStrategy;
    }
    public static MusicStreamingSystem getInstance() {
        if(MusicStreamingSystem.INSTANCE==null){
            synchronized(MusicStreamingSystem.class){
                if(MusicStreamingSystem.INSTANCE==null){
                    MusicStreamingSystem.INSTANCE = new MusicStreamingSystem(new GenreBasedRecommendationStrategy());
                }
            }
        }
        return MusicStreamingSystem.INSTANCE;
    }



    public User registerUser(String name, PlaybackStrategy playbackStrategy) {
        User user = new User(name, playbackStrategy);
        users.put(user.getId(), user);
        return user;
    }

    // Each user gets their own player
    public Player getPlayer(User user) {
        return players.computeIfAbsent(user.getId(), id -> new Player(user));
    }

    public List<Song> getRecommendations(User user) {
        return recommendationStrategy.recommend(user, catalog.getAllSongs());
    }

    public void setRecommendationStrategy(RecommendationStrategy strategy) {
        this.recommendationStrategy = strategy;
    }

    public MusicCatalog getCatalog() { return catalog; }
}
