package CarRentalSystem.factory;

import CarRentalSystem.entities.Bike;
import CarRentalSystem.entities.Car;
import CarRentalSystem.entities.SUV;
import CarRentalSystem.entities.Vehicle;
import CarRentalSystem.enums.VehicleType;

// Factory: creates the right vehicle based on type
public class VehicleFactory {
    public static Vehicle create(VehicleType type, String id, String regNo, String model, double price) {
        switch (type) {
            case CAR:  return new Car(id, regNo, model, price);
            case BIKE: return new Bike(id, regNo, model, price);
            case SUV:  return new SUV(id, regNo, model, price);
            default:   throw new IllegalArgumentException("Unknown vehicle type");
        }
    }
}