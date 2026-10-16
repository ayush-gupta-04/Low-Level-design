package CarRentalSystem.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import CarRentalSystem.entities.Vehicle;

/** Handles vehicle registration, removal, lookup, and catalog access. */
public class VehicleService {
    private final Map<String, Vehicle> vehicles = new ConcurrentHashMap<>();

    public boolean addVehicle(Vehicle vehicle) {
        if (vehicle == null || vehicle.getId() == null || vehicle.getId().trim().isEmpty()) {
            return false;
        }
        return vehicles.putIfAbsent(vehicle.getId(), vehicle) == null;
    }

    public boolean removeVehicle(String vehicleId) {
        if (vehicleId == null || vehicleId.trim().isEmpty()) {
            return false;
        }
        return vehicles.remove(vehicleId) != null;
    }

    public Optional<Vehicle> getVehicle(String vehicleId) {
        if (vehicleId == null || vehicleId.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(vehicles.get(vehicleId));
    }

    public List<Vehicle> getAllVehicles() {
        return new ArrayList<>(vehicles.values());
    }
}
