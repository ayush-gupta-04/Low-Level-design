package CarRentalSystem.entities;

import CarRentalSystem.enums.VehicleType;

public class SUV extends Vehicle {
    public SUV(String id, String regNo, String model, double price) {
        super(id, regNo, model, VehicleType.SUV, price);
    }
}