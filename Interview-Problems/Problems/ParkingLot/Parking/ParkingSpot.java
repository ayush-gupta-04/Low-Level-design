package Parking;

import Vehicle.Vehicle;
import Vehicle.VehicleSize;

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
    
    public synchronized void parkVehicle(Vehicle vehicle){
        this.parkedVehicle = vehicle;
        this.isOccupied = true;
    }
    public synchronized void unparkVehicle(){
        this.parkedVehicle = null;
        this.isOccupied = false;
    }
    public boolean canFitVehicle(Vehicle vehicle){
        switch (this.spotSize) {
            case VehicleSize.SMALL:
                return vehicle.getSize() == VehicleSize.SMALL;
            case VehicleSize.MEDIUM:
                return vehicle.getSize() == VehicleSize.MEDIUM;
            case VehicleSize.LARGE:
                return vehicle.getSize() == VehicleSize.LARGE || vehicle.getSize() == VehicleSize.MEDIUM;
            default:
                return false;
        }
    }

    // getters
    public String getSpotId(){
        return this.spotId;
    }
    public VehicleSize getSpotSize(){
        return this.spotSize;
    }
    public boolean isOccupied(){
        return this.isOccupied;
    }
    public boolean isAvailable(){
        return !this.isOccupied;
    }
}
