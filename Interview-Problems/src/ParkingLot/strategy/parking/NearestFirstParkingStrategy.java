package ParkingLot.strategy.parking;

import java.util.List;
import java.util.Optional;

import ParkingLot.Parking.ParkingFloor;
import ParkingLot.Parking.ParkingSpot;
import ParkingLot.Vehicle.Vehicle;
import ParkingLot.strategy.ParkingStrategy;

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
