package ParkingLot.strategy;

import java.util.List;
import java.util.Optional;

import ParkingLot.Vehicle.Vehicle;
import ParkingLot.entities.ParkingFloor;
import ParkingLot.entities.ParkingSpot;

public interface ParkingStrategy {
    public Optional<ParkingSpot> findSpot(List<ParkingFloor> floors, Vehicle vehicle);
} 
