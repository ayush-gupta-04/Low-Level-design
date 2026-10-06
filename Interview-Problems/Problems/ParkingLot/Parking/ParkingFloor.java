package Parking;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import Vehicle.Vehicle;

public class ParkingFloor {
    int floorNumber;
    Map<String, ParkingSpot> spots;

    public ParkingFloor(int floorNumber){
        this.spots = new HashMap<>();
        this.floorNumber = floorNumber;
    }

    public void addSpot(ParkingSpot spot){
        spots.put(spot.getSpotId(), spot);
    }
    public synchronized Optional<ParkingSpot> findAvailableSpot(Vehicle vehicle){
        return spots.values().stream()
        .filter(s -> s.isAvailable() && s.canFitVehicle(vehicle))
        .sorted(Comparator.comparing(ParkingSpot::getSpotSize))
        .findFirst();
    }
}
