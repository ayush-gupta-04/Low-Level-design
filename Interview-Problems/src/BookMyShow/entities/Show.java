package entities;

import strategies.PricingStrategy;

public class Show {
    private String id;
    private Movie movie;
    private Screen screen;
    private PricingStrategy pricingStrategy;
    private Cinema cinema;

    public Show(String id, Movie movie, Cinema cinema, Screen screen, PricingStrategy pricingStrategy) {
        this.id = id;
        this.movie = movie;
        this.screen = screen;
        this.pricingStrategy = pricingStrategy;
        this.cinema = cinema;
    }


    // getter & setter
    public String getId() {return id;}
    public Movie getMovie() {return movie;}
    public Screen getScreen() {return screen;}
    public PricingStrategy getPricingStrategy() {return pricingStrategy;}
    public Cinema getCinema(){return cinema;};
}