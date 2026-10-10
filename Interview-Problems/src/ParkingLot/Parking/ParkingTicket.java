package Parking;

import java.util.Date;
import java.util.UUID;
import Vehicle.Vehicle;

public class ParkingTicket {
    String ticketId;
    ParkingSpot spot;
    Vehicle vehicle;
    long entryTime;
    long exitTime;
    public ParkingTicket(ParkingSpot spot, Vehicle vehicle){
        this.spot = spot;
        this.vehicle = vehicle;
        this.ticketId = UUID.randomUUID().toString();
        this.entryTime = new Date().getTime();
    }

    // getter
    public String getTicketId(){return this.ticketId;}
    public ParkingSpot getSpot(){return this.spot;}
    public Vehicle getVehicle(){return this.vehicle;};
    public long getEntryTime(){return this.entryTime;}
    public long getExitTime(){return this.exitTime;}

    // setter
    public void setExitTime(){
        this.exitTime = new Date().getTime();
    }


    // printing
    public void printTicket(){
        System.out.println("------------------------Ticket-----------------------------");
        System.out.println("Ticket Id : " + this.ticketId);
        System.out.println("Spot Id : " + this.spot.getSpotId());
        System.out.println("Vehicle Lisense Number : " + this.vehicle.getLisenseNumber());
    }
}
