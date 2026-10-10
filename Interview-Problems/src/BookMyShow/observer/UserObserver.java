package observer;

import entities.Movie;
import entities.User;

public class UserObserver implements MovieObserver {
    private User user;
    public UserObserver(User user){
        this.user = user;
    }
    public void update(Movie movie){
        System.out.println("Notifying " + user.getName() + " for " + movie.getName());
    }
}
