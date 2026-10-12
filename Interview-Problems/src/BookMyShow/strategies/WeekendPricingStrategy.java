package BookMyShow.strategies;

import java.util.List;

import BookMyShow.entities.ShowSeat;

public class WeekendPricingStrategy implements PricingStrategy {
    private static final double WEEKEND_TAX_PERCENT = 20.0;

    @Override
    public double calcPrice(List<ShowSeat> seats) {
        double price = 0.0;
        for (ShowSeat showSeat : seats) {
            price += showSeat.getSeat().getType().getPrice();
        }
        return price + (price / 100.0) * WEEKEND_TAX_PERCENT;
    }
}
