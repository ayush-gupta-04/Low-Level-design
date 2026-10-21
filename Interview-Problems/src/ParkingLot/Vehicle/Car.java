package ParkingLot.Vehicle;

import ParkingLot.enums.VehicleSize;

public class Car extends Vehicle{
    public Car(String lisenseNumber){
        super(lisenseNumber, VehicleSize.MEDIUM);
    }
}
