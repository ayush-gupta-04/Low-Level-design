package ParkingLot.Parking;

import ParkingLot.Vehicle.Vehicle;
import ParkingLot.Vehicle.VehicleSize;

public class ParkingSpot {
    String spotId;
    Vehicle parkedVehicle;
    boolean isOccupied;
    VehicleSize spotSize;

    public ParkingSpot(String spotId, VehicleSize spotSize){
        this.spotId = spotId;
        this.spotSize = spotSize;
        this.isOccupied = false;
        this.parkedVehicle = null;
    }
    

    // we are making the (checking and parking) atomic.
    public synchronized boolean tryParkVehicle(Vehicle vehicle) {
        if (vehicle == null || parkedVehicle != null || !canFitVehicle(vehicle)) {
            return false;
        }
        parkedVehicle = vehicle;
        isOccupied = true;
        return true;
    }
    // synchronized unpark also.
    public synchronized void unparkVehicle(){
        this.parkedVehicle = null;
        this.isOccupied = false;
    }
    public boolean canFitVehicle(Vehicle vehicle){
        return vehicle != null && spotSize.canAccommodate(vehicle.getSize());
    }

    // getters
    public String getSpotId(){
        return this.spotId;
    }
    public VehicleSize getSpotSize(){
        return this.spotSize;
    }
    public synchronized boolean isOccupied(){
        return this.isOccupied;
    }
    public synchronized boolean isAvailable(){
        return !this.isOccupied;
    }
}
