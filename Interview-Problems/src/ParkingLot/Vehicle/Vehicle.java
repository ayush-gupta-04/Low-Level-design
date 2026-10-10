package Vehicle;

public abstract class Vehicle {
    VehicleSize size;
    String lisenseNumber;
    public Vehicle(String lisenseNumber, VehicleSize size){
        this.size = size;
        this.lisenseNumber = lisenseNumber;
    }
    public String getLisenseNumber(){
        return this.lisenseNumber;
    }
    public VehicleSize getSize(){
        return this.size;
    }
}
