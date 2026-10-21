package CarRentalSystem.entities;

import CarRentalSystem.enums.VehicleType;

public class Bike extends Vehicle {
    public Bike(String id, double price) {
        super(id, VehicleType.BIKE, price);
    }
}