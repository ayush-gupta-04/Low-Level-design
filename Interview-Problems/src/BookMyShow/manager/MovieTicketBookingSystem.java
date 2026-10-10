package manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import entities.Booking;
import entities.Cinema;
import entities.Movie;
import entities.Screen;
import entities.Seat;
import entities.Show;
import entities.User;
import strategies.PaymentStrategy;
import strategies.PricingStrategy;

public class MovieTicketBookingSystem {

    private static MovieTicketBookingSystem instance;
    private Map<String, Movie> movies;
    private Map<String, Show> shows;
    private Map<String, User> users;
    private Map<String, Cinema> cinemas;
    private BookingManager bookingMgr;

    private MovieTicketBookingSystem(){
        this.movies = new HashMap<>();
        this.shows = new HashMap<>();
        this.users = new HashMap<>();
        this.cinemas = new HashMap<>();
        this.bookingMgr = BookingManager.getInstance();
    }
    public synchronized static MovieTicketBookingSystem getInstance(){
        if(instance==null){
            MovieTicketBookingSystem.instance = new MovieTicketBookingSystem();
        }
        return MovieTicketBookingSystem.instance;
    }


    public Movie addMovie(String id, String name, int duration){
        Movie movie = new Movie(id, name, duration);
        movies.put(movie.getId(), movie);
        return movie;
    }
    public Show addShow(String id, Movie movie, Cinema cinema, Screen screen, PricingStrategy pricingStrategy){
        Show show = new Show(id, movie, cinema, screen, pricingStrategy);
        shows.put(show.getId(), show);
        return show;
    }
    public User addUser(String name , String email){
        User user = new User(name, email);
        users.put(user.getId(), user);
        return user;
    }
    public Cinema addCinema(String id, String name, String city) {
        Cinema cinema = new Cinema(id, name, city);
        cinemas.put(cinema.getId(), cinema);
        return cinema;
    }
    // searching
    public List<Show> findShowsInCinema(Cinema cinema){
        return shows.values().stream()
        .filter(show -> show.getCinema()==cinema)
        .toList();
    }

    public Optional<Booking> bookTickets(String userId, String showId, List<Seat> seats, PaymentStrategy paymentStrategy){
        return bookingMgr.createBooking(users.get(userId), shows.get(showId), seats, paymentStrategy);
    }


    public void shutdown(){
        bookingMgr.shutdown();
    }
}
