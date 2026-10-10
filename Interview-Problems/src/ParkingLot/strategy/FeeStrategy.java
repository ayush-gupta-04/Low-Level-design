package strategy;

import Parking.ParkingTicket;

public interface FeeStrategy {
    double calculateFee(ParkingTicket ticket);
}
