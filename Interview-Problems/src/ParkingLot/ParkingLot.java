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
import ParkingLot.enums.TicketStatus;
import ParkingLot.strategy.FeeStrategy;
import ParkingLot.strategy.ParkingStrategy;
import ParkingLot.strategy.PaymentStrategy;



/*
2 IMP Methods : 
1. park()
    - find spot.
    - atomically claim the spot (only 1 thread can)
    - create ticket and return.

2. unpark()
    - find ticket.
    - claim the ticket under ticket lock.
    - calc fee & set ticket status as PAYMENT_IN_PROGRESS
    - do payment (outside ticket lock)
    - acquire the ticket lock again
    - finalize the ticket (unpark, mark ticket as completed)
*/





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

            // If 2 thread reach here .. only one can claim the spot.
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

    public boolean unparkVehicle(String ticketId, PaymentStrategy paymentStrategy){

        ParkingTicket ticket = activeTickets.get(ticketId);
        if(ticket==null) return false;

        double fee;

        // I am claiming the ticket under ticket lock.
        // ThreadA : mark the ticket as PAYMENT_IN_PROGRESS
        // ThreadB : stuck here.
        synchronized(ticket){
            if(ticket.getTicketStatus() != TicketStatus.ACTIVE) return false;

            ticket.setExitTime();
            fee = feeStrategy.calculateFee(ticket);
            ticket.setTicketStatus(TicketStatus.PAYMENT_IN_PROGRESS);
        }


        // slow request .. do not keep it under ticket lock.
        boolean result = paymentStrategy.pay(fee);

        synchronized(ticket){
            if(result){
                ticket.getSpot().unparkVehicle();
                ticket.setTicketStatus(TicketStatus.COMPLETED);
                activeTickets.remove(ticketId);
                return true;
            }
            // assumption : false means the payment failed.
            //              the payment may be stuck or is being retried .. in that case ticket status must be Pending_Payment.
            ticket.setTicketStatus(TicketStatus.ACTIVE);
            return false;
        }
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