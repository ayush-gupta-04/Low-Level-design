package CarRentalSystem.entities;

import CarRentalSystem.enums.VehicleType;

public class Car extends Vehicle {
    public Car(String id, String regNo, String model, double price) {
        super(id, regNo, model, VehicleType.CAR, price);
    }
}