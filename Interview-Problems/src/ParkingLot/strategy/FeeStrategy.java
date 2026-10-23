package ParkingLot.strategy;

import ParkingLot.entities.ParkingTicket;

public interface FeeStrategy {
    double calculateFee(ParkingTicket ticket);
}
