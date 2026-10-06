import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import strategy.FeeStrategy;
import strategy.ParkingStrategy;
import Parking.ParkingTicket;
import Vehicle.Vehicle;
import Parking.ParkingFloor;
import Parking.ParkingSpot;


public class ParkingLot {
    static ParkingLot instance;
    private FeeStrategy feeStrategy;
    private ParkingStrategy parkingStrategy;
    private List<ParkingFloor> floors = new ArrayList<>();
    private Map<String, ParkingTicket> activeTickets = new HashMap<>();   // ticketId -> ParkingTicket

    static synchronized ParkingLot getInstance(){
        if(instance==null){
            ParkingLot.instance = new ParkingLot();
        }
        return ParkingLot.instance;
    }

    public Optional<ParkingTicket> parkVehicle(Vehicle vehicle){
        Optional<ParkingSpot> availableSpot = parkingStrategy.findSpot(floors, vehicle);
        if(availableSpot.isPresent()){
            // park vehicle.
            // generate ticket.
            ParkingSpot spot = availableSpot.get();
            spot.parkVehicle(vehicle);
            ParkingTicket ticket = new ParkingTicket(spot, vehicle);
            activeTickets.put(ticket.getTicketId(), ticket);
            return Optional.of(ticket);
        }

        System.out.println("No spots are present currently!");
        return Optional.empty();
    }

    public Optional<Double> unparkVehicle(String ticketId){
        ParkingTicket ticket = activeTickets.get(ticketId);

        if(ticket != null){
            ticket.setExitTime();
            ticket.getSpot().unparkVehicle();
            activeTickets.remove(ticketId);
            Double fee = feeStrategy.calculateFee(ticket);
            return Optional.of(fee);
        }
        System.out.println("No vehicle with ticket number : " + ticketId);
        return Optional.empty();
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