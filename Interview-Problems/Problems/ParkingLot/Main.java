import java.util.Optional;

import javax.smartcardio.CardException;

import Parking.ParkingFloor;
import Parking.ParkingSpot;
import Parking.ParkingTicket;
import Vehicle.Car;
import Vehicle.Vehicle;
import Vehicle.VehicleSize;
import strategy.FeeStrategy;
import strategy.ParkingStrategy;
import strategy.fee.FlatFeeStrategy;
import strategy.parking.NearestFirstParkingStrategy;

public class Main {
    public static void main(String[] args) {
        ParkingLot parkingLot = new ParkingLot();

        ParkingStrategy ps = new NearestFirstParkingStrategy();
        FeeStrategy fs = new FlatFeeStrategy();
        ParkingFloor floor1 = new ParkingFloor(1);
        floor1.addSpot(new ParkingSpot("F1-S1", VehicleSize.SMALL));
        floor1.addSpot(new ParkingSpot("F1-S2", VehicleSize.SMALL));
        floor1.addSpot(new ParkingSpot("F1-M1", VehicleSize.MEDIUM));
        floor1.addSpot(new ParkingSpot("F1-L1", VehicleSize.LARGE));
        parkingLot.addFloor(floor1);
        parkingLot.setFeeStrategy(fs);
        parkingLot.setParkingStrategy(ps);


        Vehicle car1 = new Car("car-1", VehicleSize.MEDIUM);
        Vehicle bike1 = new Car("bike-1", VehicleSize.SMALL);
        Vehicle bike2 = new Car("bike-2", VehicleSize.SMALL);
        Vehicle truck1 = new Car("truck-1", VehicleSize.LARGE);
        Vehicle car2 = new Car("car-2", VehicleSize.MEDIUM);

        Optional<ParkingTicket> t1 = parkingLot.parkVehicle(bike1);
        if(t1.isPresent()){
            ParkingTicket ticket = t1.get();
            ticket.printTicket();
        }

        Optional<ParkingTicket> t2 = parkingLot.parkVehicle(bike2);
        if(t2.isPresent()){
            ParkingTicket ticket = t2.get();
            ticket.printTicket();
        }
    }
}
