package ParkingLot.Parking;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import ParkingLot.Vehicle.Vehicle;


public class ParkingFloor {
    private int floorNumber;
    private Map<String, ParkingSpot> spots;

    public ParkingFloor(int floorNumber){
        this.spots = new ConcurrentHashMap<>();
        this.floorNumber = floorNumber;
    }

    public void addSpot(ParkingSpot spot){
        spots.put(spot.getSpotId(), spot);
    }
    public synchronized Optional<ParkingSpot> findAvailableSpot(Vehicle vehicle){
        return spots.values().stream()
        .filter(s -> s.isAvailable() && s.canFitVehicle(vehicle))
        .sorted((a,b) -> Integer.compare(a.getSpotSize().getRank(), b.getSpotSize().getRank()))
        .findFirst();
    }
}
