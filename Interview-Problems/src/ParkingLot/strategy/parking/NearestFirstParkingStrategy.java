package strategy.parking;

import java.util.List;
import java.util.Optional;

import Parking.ParkingFloor;
import Parking.ParkingSpot;
import Vehicle.Vehicle;
import strategy.ParkingStrategy;

public class NearestFirstParkingStrategy implements ParkingStrategy {
    public Optional<ParkingSpot> findSpot(List<ParkingFloor> floors, Vehicle vehicle){
        for(ParkingFloor floor : floors){
            Optional<ParkingSpot> spot = floor.findAvailableSpot(vehicle);
            if(spot.isPresent()){
                return spot;
            }
        }
        return Optional.empty();
    }
}
