package CarRentalSystem.factory;

import java.util.Optional;

import CarRentalSystem.entities.Bike;
import CarRentalSystem.entities.Car;
import CarRentalSystem.entities.SUV;
import CarRentalSystem.entities.Vehicle;
import CarRentalSystem.enums.VehicleType;

// Factory: creates the right vehicle based on type.
// Returns Optional.empty() if the requested type is missing or unsupported.
public class VehicleFactory {
    private VehicleFactory() {
        // Utility class.
    }

    public static Optional<Vehicle> create(VehicleType type, String id, String regNo,
                                           String model, double price) {
        if (type == null || id == null || id.trim().isEmpty()
                || regNo == null || regNo.trim().isEmpty()
                || model == null || model.trim().isEmpty() || price < 0) {
            return Optional.empty();
        }

        switch (type) {
            case CAR:
                return Optional.of(new Car(id, regNo, model, price));
            case BIKE:
                return Optional.of(new Bike(id, regNo, model, price));
            case SUV:
                return Optional.of(new SUV(id, regNo, model, price));
            default:
                return Optional.empty();
        }
    }
}
