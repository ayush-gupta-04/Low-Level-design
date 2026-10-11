package strategy.fee;

import Parking.ParkingTicket;
import strategy.FeeStrategy;

public class FlatFeeStrategy implements FeeStrategy {
    private final double RATE = 100.0;

    public double calculateFee(ParkingTicket ticket){
        long entry = ticket.getEntryTime();
        long exit = ticket.getExitTime();
        long duration = exit - entry;
        return (duration/60L*60*1000.0)*RATE;
    }
}
