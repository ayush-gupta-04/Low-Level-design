# Music Streaming Service (LLD)

Browse/search songs, play them with a per-user player, follow artists, get recommendations.

## Classes
| Class | Responsibility |
|---|---|
| `MusicStreamingSystem` | Facade (singleton). Holds users, one `Player` per user, recommendation strategy |
| `MusicCatalog` | Stores songs/albums/artists and searches them |
| `Song`, `Album`, `Playlist` | `Playable`s - anything the player can load |
| `Artist` | Observable: notifies followers on a new album |
| `User` | Observer; owns history, followed artists, playlists, playback strategy |
| `Player` + `PlayerState` | Per-user player; Stopped / Playing / Paused states |

## Patterns (only where they earn their place)
- **State** - `Player` behaviour for play / pause / next / seek depends on Stopped, Playing or Paused.
- **Strategy** - `PlaybackStrategy` (free: ad every 3 songs, premium: none), swappable on upgrade.
  `RecommendationStrategy` (genre-based now, easy to add others).
- **Observer** - `User` follows `Artist`, gets notified on `releaseAlbum`.
- **Singleton** - only the `MusicStreamingSystem` facade.

## Run
```
javac -d out $(find musicstreamingservice -name '*.java')
java -cp out musicstreamingservice.MusicStreamingDemo
```

## Easy extensions
Collaborative playlists (`Playlist` gets owners), offline playback (new state/strategy),
new recommendation algorithm (implement `RecommendationStrategy`).
