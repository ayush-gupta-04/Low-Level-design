package strategy.fee;

import Parking.ParkingTicket;
import strategy.FeeStrategy;

public class FlatFeeStrategy implements FeeStrategy {
    private final double RATE = 100.0;
    public double calculateFee(ParkingTicket ticket){
        long entry = ticket.getEntryTime();
        long exit = ticket.getExitTime();
        return ((exit-entry)/60*60*1000.0)*RATE;
    }
}
