package CarRentalSystem.entities;

import CarRentalSystem.enums.VehicleType;

public class Bike extends Vehicle {
    public Bike(String id, String regNo, String model, double price) {
        super(id, regNo, model, VehicleType.BIKE, price);
    }
}