package strategies;

import java.util.List;
import entities.Seat;

public class WeekendPricingStrategy implements PricingStrategy {
    final double WEEKEND_TAX_PERCENT = 20.0;

    public double calcPrice(List<Seat> seats){
        double price = 0.0;
        for(Seat s : seats){
            price += s.getType().getPrice();
        }
        return price + (price/100.0)*WEEKEND_TAX_PERCENT;
    }
}
