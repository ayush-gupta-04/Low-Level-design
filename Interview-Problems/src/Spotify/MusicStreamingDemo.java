package Spotify;

import Spotify.entities.Album;
import Spotify.entities.Artist;
import Spotify.entities.MusicCatalog;
import Spotify.entities.Playlist;
import Spotify.entities.Song;
import Spotify.entities.User;
import Spotify.enums.Genre;
import Spotify.player.Player;
import Spotify.strategies.playback.FreePlaybackStrategy;
import Spotify.strategies.playback.PremiumPlaybackStrategy;

public class MusicStreamingDemo {
    public static void main(String[] args) {
        MusicStreamingSystem system = MusicStreamingSystem.getInstance();
        MusicCatalog catalog = system.getCatalog();

        // --- Catalog ---
        Artist daftPunk = catalog.addArtist("Daft Punk");
        Artist queen = catalog.addArtist("Queen");

        Song s1 = catalog.addSong("One More Time", daftPunk, Genre.ELECTRONIC, 320);
        Song s2 = catalog.addSong("Aerodynamic", daftPunk, Genre.ELECTRONIC, 212);
        Song s3 = catalog.addSong("Digital Love", daftPunk, Genre.ELECTRONIC, 301);
        Song s4 = catalog.addSong("Face to Face", daftPunk, Genre.ELECTRONIC, 240);
        catalog.addSong("Bohemian Rhapsody", queen, Genre.ROCK, 354);

        Album discovery = catalog.addAlbum("Discovery", daftPunk);
        for (Song s : new Song[]{s1, s2, s3, s4}) discovery.addSong(s);

        User alice = system.registerUser("Alice", new FreePlaybackStrategy());
        User bob = system.registerUser("Bob", new PremiumPlaybackStrategy());

        // --- Observer: follow an artist, get notified on release ---
        System.out.println("--- Follow artist ---");
        bob.follow(daftPunk);
        daftPunk.releaseAlbum(discovery);

        // --- State + Strategy: free user (ads), skip while paused, seek ---
        System.out.println("\n--- Alice (free) plays the album ---");
        Player alicePlayer = system.getPlayer(alice);
        alicePlayer.load(discovery);
        alicePlayer.play();
        alicePlayer.seek(100);
        alicePlayer.next();
        alicePlayer.pause();
        alicePlayer.next();     // skipping while paused starts the next song
        alicePlayer.next();     // 4th song -> ad for a free user
        alicePlayer.next();     // end of queue

        System.out.println("\n--- Alice upgrades to premium ---");
        alice.setPlaybackStrategy(new PremiumPlaybackStrategy());
        alicePlayer.load(discovery);
        alicePlayer.play();

        // --- Playlist (any Playable can be loaded) ---
        System.out.println("\n--- Bob plays his playlist ---");
        Playlist mix = bob.createPlaylist("My Mix");
        mix.addSong(s3);
        mix.addSong(s1);
        Player bobPlayer = system.getPlayer(bob);
        bobPlayer.load(mix);
        bobPlayer.play();
        bobPlayer.next();

        // --- Search & recommendations ---
        System.out.println("\n--- Search & recommendations ---");
        System.out.println("Search 'love': " + catalog.searchSongs("love"));
        System.out.println("Recommended for Bob: " + system.getRecommendations(bob));
    }
}
