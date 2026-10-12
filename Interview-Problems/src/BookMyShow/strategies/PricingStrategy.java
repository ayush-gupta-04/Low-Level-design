package BookMyShow.strategies;

import java.util.List;

import BookMyShow.entities.ShowSeat;

public interface PricingStrategy {
    double calcPrice(List<ShowSeat> seats);
}
