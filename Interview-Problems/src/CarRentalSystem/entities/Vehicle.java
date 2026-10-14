package CarRentalSystem.entities;

import CarRentalSystem.enums.VehicleStatus;
import CarRentalSystem.enums.VehicleType;

public abstract class Vehicle {
    private final String id;
    private final String registrationNo;
    private final String model;
    private final VehicleType type;
    private final double pricePerDay;
    private VehicleStatus status = VehicleStatus.AVAILABLE;
 
    public Vehicle(String id, String registrationNo, String model, VehicleType type, double pricePerDay) {
        this.id = id;
        this.registrationNo = registrationNo;
        this.model = model;
        this.type = type;
        this.pricePerDay = pricePerDay;
    }
 
    public String getId() { return id; }
    public String getModel() { return model; }
    public VehicleType getType() { return type; }
    public double getPricePerDay() { return pricePerDay; }
    public VehicleStatus getStatus() { return status; }
    public void setStatus(VehicleStatus status) { this.status = status; }
 
    @Override
    public String toString() {
        return type + " " + model + " (" + registrationNo + ") Rs." + pricePerDay + "/day";
    }
}