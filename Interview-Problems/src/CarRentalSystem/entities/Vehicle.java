package CarRentalSystem.entities;

import CarRentalSystem.enums.VehicleStatus;
import CarRentalSystem.enums.VehicleType;

public abstract class Vehicle {
    private final String id;
    private final VehicleType type;
    private final double pricePerDay;
    private VehicleStatus status = VehicleStatus.AVAILABLE;
 
    public Vehicle(String id,VehicleType type, double pricePerDay) {
        this.id = id;
        this.type = type;
        this.pricePerDay = pricePerDay;
    }
 
    public String getId() { return id; }
    public VehicleType getType() { return type; }
    public double getPricePerDay() { return pricePerDay; }
    public VehicleStatus getStatus() { return status; }
    public void setStatus(VehicleStatus status) { this.status = status; }
 
    @Override
    public String toString() {
        return type + " Rs." + pricePerDay + "/day";
    }
}