package ParkingLot.entities;

import java.util.Date;
import java.util.UUID;

import ParkingLot.Vehicle.Vehicle;
import ParkingLot.enums.TicketStatus;

public class ParkingTicket {
    String ticketId;
    ParkingSpot spot;
    Vehicle vehicle;
    long entryTime;
    long exitTime;
    TicketStatus status;

    public ParkingTicket(ParkingSpot spot, Vehicle vehicle){
        this.spot = spot;
        this.vehicle = vehicle;
        this.ticketId = UUID.randomUUID().toString();
        this.entryTime = new Date().getTime();
        this.status = TicketStatus.ACTIVE;
    }

    // -------------- Ticket exit ----------------------
    public synchronized boolean tryBeginExit(){
        if(this.status != TicketStatus.ACTIVE) return false;
        this.status = TicketStatus.PROCESSING;
        return true;
    }

    // getter
    public String getTicketId(){return this.ticketId;}
    public ParkingSpot getSpot(){return this.spot;}
    public Vehicle getVehicle(){return this.vehicle;};
    public long getEntryTime(){return this.entryTime;}
    public long getExitTime(){return this.exitTime;}
    public TicketStatus getTicketStatus(){return this.status;}

    // setter
    public void setExitTime(){
        this.exitTime = new Date().getTime();
    }
    public void setTicketStatus(TicketStatus status){
        this.status = status;
    }


    // printing
    public void printTicket(){
        System.out.println("------------------------Ticket-----------------------------");
        System.out.println("Ticket Id : " + this.ticketId);
        System.out.println("Spot Id : " + this.spot.getSpotId());
        System.out.println("Vehicle Lisense Number : " + this.vehicle.getLisenseNumber());
    }
}
