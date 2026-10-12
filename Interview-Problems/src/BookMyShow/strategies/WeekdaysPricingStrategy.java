package BookMyShow.strategies;

import java.util.List;

import BookMyShow.entities.ShowSeat;

public class WeekdaysPricingStrategy implements PricingStrategy {
    @Override
    public double calcPrice(List<ShowSeat> seats) {
        double price = 0.0;
        for (ShowSeat showSeat : seats) {
            price += showSeat.getSeat().getType().getPrice();
        }
        return price;
    }
}
