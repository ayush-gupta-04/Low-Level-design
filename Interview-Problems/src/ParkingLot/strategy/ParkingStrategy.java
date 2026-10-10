package strategy;

import java.util.List;
import java.util.Optional;

import Parking.ParkingFloor;
import Parking.ParkingSpot;
import Vehicle.Vehicle;

public interface ParkingStrategy {
    public Optional<ParkingSpot> findSpot(List<ParkingFloor> floors, Vehicle vehicle);
} 
