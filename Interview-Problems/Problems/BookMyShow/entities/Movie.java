package entities;

import observer.MovieObservable;

public class Movie extends MovieObservable {
    String id;
    String name;
    int duration;

    public Movie(String id, String name, int duration){
        this.id = id;
        this.name = name;
        this.duration = duration;
    }

    public String getId() {return id;}
    public String getName() {return name;}
    public int getDuration() {return duration;}
}
