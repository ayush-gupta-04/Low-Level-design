package observer;

import java.util.ArrayList;
import java.util.List;
import entities.Movie;

public class MovieObservable {
    List<MovieObserver> observers;

    public MovieObservable(){
        this.observers = new ArrayList<>();
    }

    public void addObserver(MovieObserver observer){
        observers.add(observer);
    }
    public void notifyObservers(){
        for(MovieObserver obv : observers){
            obv.update((Movie) this);
        }
    }
    public void removeObserver(MovieObserver observer){
        observers.remove(observer);
    }
}
