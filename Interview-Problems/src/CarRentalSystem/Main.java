package CarRentalSystem;

import java.time.LocalDate;
import java.util.Optional;

import CarRentalSystem.entities.Customer;
import CarRentalSystem.entities.Reservation;
import CarRentalSystem.entities.Vehicle;
import CarRentalSystem.enums.VehicleType;
import CarRentalSystem.factory.VehicleFactory;
import CarRentalSystem.strategies.payment.UpiPayment;

public class Main {
    public static void main(String[] args) {
        RentalSystem system = new RentalSystem();

        // Add vehicles only when the factory successfully creates them.
        addVehicle(system, VehicleFactory.create(VehicleType.CAR, "V1", "KA01-1111", "Honda City", 2000));
        addVehicle(system, VehicleFactory.create(VehicleType.CAR, "V2", "KA01-2222", "Swift", 1500));
        addVehicle(system, VehicleFactory.create(VehicleType.BIKE, "V3", "KA01-3333", "Royal Enfield", 800));

        Customer alice = new Customer("C1", "Alice", "DL-12345");
        Customer bob = new Customer("C2", "Bob", "DL-67890");

        LocalDate start = LocalDate.of(2026, 11, 1);
        LocalDate end = LocalDate.of(2026, 11, 4);

        // 1. Search
        System.out.println("Available cars: " + system.search(VehicleType.CAR, start, end));

        // 2. Reserve
        Reservation r1 = system.reserve(alice, "V1", start, end).orElse(null);
        if (r1 == null) {
            System.out.println("Booking failed: vehicle or date range is invalid/unavailable.");
            return;
        }

        // 3. Double booking is prevented without throwing an exception.
        Optional<Reservation> overlapping = system.reserve(
                bob, "V1", LocalDate.of(2026, 11, 3), LocalDate.of(2026, 11, 5));
        if (!overlapping.isPresent()) {
            System.out.println("Booking failed: vehicle is unavailable for those dates.");
        }

        // 4. Search again: V1 is no longer available
        System.out.println("Available cars: " + system.search(VehicleType.CAR, start, end));

        // 5. Pickup
        if (!system.pickup(r1.getId())) {
            System.out.println("Pickup failed: reservation was not in a pickup-ready state.");
            return;
        }

        // 6. Return 2 days late and pay
        if (!system.returnVehicle(r1.getId(), LocalDate.of(2026, 11, 6), new UpiPayment()).isPresent()) {
            System.out.println("Return/payment failed. Reservation remains ongoing; retry payment.");
        }

        // 7. Cancel flow
        Reservation r2 = system.reserve(bob, "V2", start, end).orElse(null);
        if (r2 != null) {
            if (!system.cancel(r2.getId())) {
                System.out.println("Cancellation failed: reservation was not cancellable.");
            }
        }
    }

    private static void addVehicle(RentalSystem system, Optional<Vehicle> maybeVehicle) {
        Vehicle vehicle = maybeVehicle.orElse(null);
        if (vehicle != null && !system.addVehicle(vehicle)) {
            System.out.println("Could not add vehicle: " + vehicle.getId());
        }
    }
}
