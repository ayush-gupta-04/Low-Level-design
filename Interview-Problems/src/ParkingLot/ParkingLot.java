package ParkingLot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import ParkingLot.Parking.ParkingFloor;
import ParkingLot.Parking.ParkingSpot;
import ParkingLot.Parking.ParkingTicket;
import ParkingLot.Vehicle.Vehicle;
import ParkingLot.strategy.FeeStrategy;
import ParkingLot.strategy.ParkingStrategy;

public class ParkingLot {
    static ParkingLot instance;
    private FeeStrategy feeStrategy;
    private ParkingStrategy parkingStrategy;
    private List<ParkingFloor> floors = new ArrayList<>();
    private Map<String, ParkingTicket> activeTickets = new ConcurrentHashMap<>();   // ticketId -> ParkingTicket

    static synchronized ParkingLot getInstance(){
        if(instance==null){
            ParkingLot.instance = new ParkingLot();
        }   
        return ParkingLot.instance;
    }


    /*
        we can have lock on this whole function.
        if we have lock on the whole function then, it will searialise the whole operation.
        but we want to make it concurrent with isolation.
    */
    public Optional<ParkingTicket> parkVehicle(Vehicle vehicle){
        while(true){
            // two threads may find the same candidate spot.
            Optional<ParkingSpot> candidate = parkingStrategy.findSpot(floors, vehicle);

            if(candidate.isEmpty()){
                return Optional.empty();
            }

            ParkingSpot spot = candidate.get();

            if (!spot.tryParkVehicle(vehicle)) {
                // Another thread may have taken this spot.
                // Search again for an available spot.
                continue;
            }

            ParkingTicket ticket = new ParkingTicket(spot, vehicle);
            activeTickets.put(ticket.getTicketId(), ticket);

            return Optional.of(ticket);
        }
    }

    public Optional<Double> unparkVehicle(String ticketId){

        // if we do activeTickets.get() .. 2 thread might get the same ticket and the logic will run twice.
        // If we remove it right away, it ensures ticket is not available for the next thread.
        ParkingTicket ticket = activeTickets.remove(ticketId);

        // only one thread can pass this check.
        if (ticket == null) {
            System.out.println("No vehicle with ticket number : " + ticketId);
            return Optional.empty();
        }

        ticket.setExitTime();
        double fee = feeStrategy.calculateFee(ticket);
        ticket.getSpot().unparkVehicle();
        return Optional.of(fee);
    }

    public void setFeeStrategy(FeeStrategy feeStrategy){
        this.feeStrategy = feeStrategy;
    }
    public void setParkingStrategy(ParkingStrategy parkingStrategy){
        this.parkingStrategy = parkingStrategy;
    }
    public void addFloor(ParkingFloor floor){
        floors.add(floor);
    }

}