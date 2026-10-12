package ParkingLot.strategy;

import java.util.List;
import java.util.Optional;

import ParkingLot.Parking.ParkingFloor;
import ParkingLot.Parking.ParkingSpot;
import ParkingLot.Vehicle.Vehicle;

public interface ParkingStrategy {
    public Optional<ParkingSpot> findSpot(List<ParkingFloor> floors, Vehicle vehicle);
} 
