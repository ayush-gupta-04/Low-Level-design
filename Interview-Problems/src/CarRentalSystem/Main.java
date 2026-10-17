package CarRentalSystem;

import java.time.LocalDate;
import java.util.Optional;

import CarRentalSystem.entities.Bike;
import CarRentalSystem.entities.Car;
import CarRentalSystem.entities.Customer;
import CarRentalSystem.entities.Reservation;
import CarRentalSystem.enums.VehicleType;
import CarRentalSystem.services.BillingService;
import CarRentalSystem.services.RentalService;
import CarRentalSystem.services.ReservationService;
import CarRentalSystem.strategies.payment.UpiPayment;

public class Main {
    public static void main(String[] args) {
        BillingService billingService = new BillingService();
        ReservationService reservationService = new ReservationService();
        RentalService rentalService = new RentalService(reservationService, billingService);
        RentalSystem system = new RentalSystem(reservationService, rentalService);




        system.addVehicle(new Car("V1", "KA01-1111", "Honda City", 2000));
        system.addVehicle(new Car("V2", "KA01-1111", "Swift", 1500));
        system.addVehicle(new Bike("V3", "KA01-1111", "Royal Enfield", 800));

        Customer alice = new Customer("C1", "Alice", "DL-12345");
        Customer bob = new Customer("C2", "Bob", "DL-67890");

        system.addCustomer(alice);
        system.addCustomer(bob);
        LocalDate start = LocalDate.of(2026, 11, 1);
        LocalDate end = LocalDate.of(2026, 11, 4);

        // 1. Search
        System.out.println("Available cars: " + system.searchAvailableVehicles(VehicleType.CAR, start, end));

        // 2. Reserve
        Optional<Reservation> r1 = system.createReservation(alice.getId(), "V1", start, end);
        if (r1.isEmpty()) {
            System.out.println("Booking failed: vehicle or date range is invalid/unavailable.");
            return;
        }

        // 3. Double booking is prevented without throwing an exception.
        Optional<Reservation> overlapping = system.createReservation(bob.getId(), "V1", LocalDate.of(2026, 11, 3), LocalDate.of(2026, 11, 5));
        if (!overlapping.isPresent()) {
            System.out.println("Booking failed: vehicle is unavailable for those dates.");
        }

        // 4. Search again: V1 is no longer available
        System.out.println("Available cars: " + system.searchAvailableVehicles(VehicleType.CAR, start, end));

        // 5. Pickup
        if (!system.pickupVehicle(r1.get().getId())) {
            System.out.println("Pickup failed: reservation was not in a pickup-ready state.");
            return;
        }

        // 6. Return 2 days late and pay
        if (!system.returnVehicle(r1.get().getId(), LocalDate.of(2026, 11, 6), new UpiPayment()).isPresent()) {
            System.out.println("Return/payment failed. Reservation remains ongoing; retry payment.");
        }

        // 7. Cancel flow
        Optional<Reservation> r2 = system.createReservation(bob.getId(), "V2", start, end);
        if (r2 != null) {
            if (!system.cancelReservation(r2.get().getId())) {
                System.out.println("Cancellation failed: reservation was not cancellable.");
            }
        }
    }

}
