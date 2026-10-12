package ParkingLot.strategy;

import ParkingLot.Parking.ParkingTicket;

public interface FeeStrategy {
    double calculateFee(ParkingTicket ticket);
}
