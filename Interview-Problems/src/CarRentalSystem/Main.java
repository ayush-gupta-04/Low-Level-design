package CarRentalSystem;

import java.time.LocalDate;

import CarRentalSystem.entities.Customer;
import CarRentalSystem.entities.Reservation;
import CarRentalSystem.enums.VehicleType;
import CarRentalSystem.factory.VehicleFactory;
import CarRentalSystem.strategies.payment.UpiPayment;

public class Main {
    public static void main(String[] args) {
        RentalSystem system = new RentalSystem();
 
        // Add vehicles
        system.addVehicle(VehicleFactory.create(VehicleType.CAR, "V1", "KA01-1111", "Honda City", 2000));
        system.addVehicle(VehicleFactory.create(VehicleType.CAR, "V2", "KA01-2222", "Swift", 1500));
        system.addVehicle(VehicleFactory.create(VehicleType.BIKE, "V3", "KA01-3333", "Royal Enfield", 800));
 
        Customer alice = new Customer("C1", "Alice", "DL-12345");
        Customer bob = new Customer("C2", "Bob", "DL-67890");
 
        LocalDate start = LocalDate.of(2026, 11, 1);
        LocalDate end = LocalDate.of(2026, 11, 4);
 
        // 1. Search
        System.out.println("Available cars: " + system.search(VehicleType.CAR, start, end));
 
        // 2. Reserve
        Reservation r1 = system.reserve(alice, "V1", start, end);
 
        // 3. Double booking is prevented
        try {
            system.reserve(bob, "V1", LocalDate.of(2026, 11, 3), LocalDate.of(2026, 11, 5));
        } catch (IllegalStateException e) {
            System.out.println("Booking failed: " + e.getMessage());
        }
 
        // 4. Search again: V1 is no longer available
        System.out.println("Available cars: " + system.search(VehicleType.CAR, start, end));
 
        // 5. Pickup
        system.pickup(r1.getId());
 
        // 6. Return 2 days late and pay
        system.returnVehicle(r1.getId(), LocalDate.of(2026, 11, 6), new UpiPayment());
 
        // 7. Cancel flow
        Reservation r2 = system.reserve(bob, "V2", start, end);
        system.cancel(r2.getId());
    }
}
