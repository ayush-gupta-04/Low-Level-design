package ParkingLot;

import java.util.Optional;

import ParkingLot.Vehicle.Car;
import ParkingLot.Vehicle.Bike;
import ParkingLot.Vehicle.Truck;
import ParkingLot.Parking.ParkingFloor;
import ParkingLot.Parking.ParkingSpot;
import ParkingLot.Parking.ParkingTicket;
import ParkingLot.Vehicle.Vehicle;
import ParkingLot.enums.VehicleSize;
import ParkingLot.strategy.FeeStrategy;
import ParkingLot.strategy.ParkingStrategy;
import ParkingLot.strategy.fee.FlatFeeStrategy;
import ParkingLot.strategy.parking.NearestFirstParkingStrategy;
import ParkingLot.strategy.payment.CashPaymentStrategy;

public class Main {
    public static void main(String[] args) throws InterruptedException{
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


        Vehicle car1 = new Car("car-1");
        Vehicle bike1 = new Bike("bike-1");
        Vehicle bike2 = new Bike("bike-2");
        Vehicle truck1 = new Truck("truck-1");
        Vehicle car2 = new Car("car-2");

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

        parkingLot.unparkVehicle(t1.get().getTicketId(), new CashPaymentStrategy());
    }
}
