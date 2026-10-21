package CarRentalSystem.entities;

import CarRentalSystem.enums.VehicleType;

public class Car extends Vehicle {
    public Car(String id, double price) {
        super(id, VehicleType.CAR, price);
    }
}