package strategies;

import java.util.List;
import entities.Seat;

public interface PricingStrategy {
    public double calcPrice(List<Seat> seats);
} 
