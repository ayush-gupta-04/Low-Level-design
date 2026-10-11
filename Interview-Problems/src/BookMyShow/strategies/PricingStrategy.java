package strategies;

import java.util.List;
import entities.ShowSeat;

public interface PricingStrategy {
    double calcPrice(List<ShowSeat> seats);
}
