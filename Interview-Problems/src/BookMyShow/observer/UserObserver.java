package BookMyShow.observer;

import BookMyShow.entities.Movie;
import BookMyShow.entities.User;

public class UserObserver implements MovieObserver {
    private User user;
    public UserObserver(User user){
        this.user = user;
    }
    public void update(Movie movie){
        System.out.println("Notifying " + user.getName() + " for " + movie.getName());
    }
}
