package strategies;

import java.util.List;
import entities.Seat;

public class WeekdaysPricingStrategy implements PricingStrategy{
    public double calcPrice(List<Seat> seats){
        double price = 0.0;
        for(Seat s : seats){
            price += s.getType().getPrice();
        }
        return price;
    }
}
