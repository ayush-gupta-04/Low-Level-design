package ParkingLot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import ParkingLot.Vehicle.Vehicle;
import ParkingLot.entities.ParkingFloor;
import ParkingLot.entities.ParkingSpot;
import ParkingLot.entities.ParkingTicket;
import ParkingLot.entities.Payment;
import ParkingLot.enums.PaymentStatus;
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

    
        // I am claiming the ticket under ticket lock.
        // ThreadA : mark the ticket as PROCESSING
        // ThreadB : stuck here.
        if(!ticket.tryBeginExit()){
            System.out.println("Ticket exit in Process !");
            return false;
        }

        ticket.setExitTime();
        double fee = feeStrategy.calculateFee(ticket);

        try {
            Payment payment = paymentStrategy.pay(fee);   // we generally have a Timeout here .. example 1 min.

            // surely failed .. roll back .. mark ticket as ACTIVE
            if(payment != null && payment.getStatus() == PaymentStatus.FAILED){
                ticket.setTicketStatus(TicketStatus.ACTIVE);
                return false;
            }
            
            // If payment is PENDING or null or UNKNOWN: 
            //  - For now: leave ticket in PROCESSING & return false;
            //  - Reconcilation Process will it resolve it later in background.

        } catch (Exception e) {
            // Reason of failure can be anything.
            // We must check the payment status, and act accordingly.
            // There can be three cases .. SUCCESS, FAILURE, (PENDING or null or UNKNOWN)
            return false;
        }

        // The payment Succeeded .. Finalize the Ticket.
        ticket.getSpot().unparkVehicle();
        ticket.setTicketStatus(TicketStatus.COMPLETED);
        activeTickets.remove(ticketId);
        return true;
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



// One distinction : 
// - BookMyShow locks a free seat that other users want, so the lock needs a TTL. 
//   Otherwise one abandoned checkout blocks everyone else.
// - Unpark claims a ticket for a spot that is already occupied. 
//   Nobody else is competing for it, and PROCESSING only blocks that one ticket, so a missing timer doesn’t hurt others. 
//   The risk is a hung pay() leaving the ticket stuck in PROCESSING forever.



// Timeout in Pay()
//    - The payment request should have a timeout so your application doesn't wait indefinitely.
//    - If Request times out, Treat the payment outcome as unknown and reconcile it with the gateway.



// 2 Edge cases.

// 1. Payment succeeded but threw an error

//     - Payment gateway charges the customer
//       ₹200 is successfully deducted from the customer's account.
//     - Your application never receives a valid response
//       The network times out, or parsing the response throws an exception.
//     - Your code enters catch
//       Your application doesn't know whether the customer was charged.

// What should we do?
// Do not blindly restore the ticket to ACTIVE, and do not charge the customer again with a new payment request.
// Instead, use a payment reconciliation flow.
// Your catch block should try to determine the payment's real status using a stable payment attempt ID or idempotency key.

// Gateway status	           What your Parking Lot should do
// SUCCESS	                   Complete the exit and release the parking spot.
// FAILED	                   Restore the ticket to ACTIVE, allowing the customer to retry payment.
// PENDING/UNKNOWN	           Keep the ticket in PROCESSING and check again later.



// 2. Payment is hung
//    - Run the payment with a timeout so your thread isn’t stuck.
//      But it only interrupts your thread.
//      The gateway may still be processing, so a timeout is the same unknown outcome as case 1: verify before rolling back.

//    - If FAILED :
//         - Roll Back Immediately (mark ticket as ACTIVE)
//    - If UNKNOWN: 
//         - gateway unreachable, can't tell if charged.
//         - Stay in PROCESSING. 
//         - Do NOT roll back Immediately.
//         - A background reconciliation resolves it later.
//    - If SUCCESS : 
//         - Free Spot.
//         - Mark ticket as COMPLETE.
//         - remove tickets from activeTickets.

// The reconciliation
//     Your system keeps the ticket in PROCESSING.
//     A background worker periodically checks the gateway, or processes a payment webhook, until the actual payment outcome is known.