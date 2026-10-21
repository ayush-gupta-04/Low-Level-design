package ParkingLot.Vehicle;

import ParkingLot.enums.VehicleSize;

public class Truck extends Vehicle{
    public Truck(String lisenseNumber){
        super(lisenseNumber, VehicleSize.LARGE);
    }
}
