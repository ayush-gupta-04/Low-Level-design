package CarRentalSystem.entities;

import CarRentalSystem.enums.VehicleType;

public class SUV extends Vehicle {
    public SUV(String id, double price) {
        super(id, VehicleType.SUV, price);
    }
}