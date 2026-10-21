package ParkingLot.Vehicle;

import ParkingLot.enums.VehicleSize;

public class Bike extends Vehicle{
    public Bike(String lisenseNumber){
        super(lisenseNumber, VehicleSize.SMALL);
    }
}