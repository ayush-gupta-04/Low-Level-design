package CarRentalSystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import CarRentalSystem.entities.Bill;
import CarRentalSystem.entities.Customer;
import CarRentalSystem.entities.Reservation;
import CarRentalSystem.entities.Vehicle;
import CarRentalSystem.enums.VehicleType;
import CarRentalSystem.services.RentalService;
import CarRentalSystem.services.ReservationService;
import CarRentalSystem.strategies.PaymentStrategy;
import CarRentalSystem.strategies.payment.UpiPayment;

/** Facade: exposes the car-rental use cases and delegates the work to services. */
public class RentalSystem {
    private final Map<String, Customer> customers =  new HashMap<>();
    private final Map<String, Vehicle> vehicles = new ConcurrentHashMap<>();
    private final ReservationService reservationService;
    private final RentalService rentalService;


    /** Allows services to be wired explicitly, for example in tests. */
    public RentalSystem(ReservationService reservationService, RentalService rentalService) {
        this.reservationService = reservationService;
        this.rentalService = rentalService;
    }

    // main methods
    public Optional<Reservation> createReservation(String customerId, String vehicleId, LocalDate start, LocalDate end) {
        Customer customer = customers.get(customerId);
        Vehicle vehicle = vehicles.get(vehicleId);
        if(vehicle == null || customer == null) return Optional.empty();

        return reservationService.createReservation(customer, vehicle, start, end, new UpiPayment());
    }

    public boolean cancelReservation(String reservationId) {
        return reservationService.cancelReservation(reservationId);
    }

    public boolean pickupVehicle(String reservationId) {
        return rentalService.pickup(reservationId);
    }

    public Optional<Bill> returnVehicle(String reservationId, LocalDate returnDate, PaymentStrategy paymentStrategy) {
        return rentalService.returnVehicle(reservationId, returnDate, paymentStrategy);
    }
    // end of main methods.



    // extra methods.
    public void addVehicle(Vehicle vehicle) {
        vehicles.put(vehicle.getId(),vehicle);
    }

    public void removeVehicle(String vehicleId) {
        vehicles.remove(vehicleId);
    }
    public void addCustomer(Customer customer){
        customers.put(customer.getId(), customer);
    }
    public void removeCustomer(String customerId){
        customers.remove(customerId);
    }

    public List<Vehicle> searchAvailableVehicles(VehicleType type, LocalDate start, LocalDate end) {
        List<Vehicle> availableVehicles = new ArrayList<>();

        for(Vehicle vehicle : vehicles.values()){
            if(vehicle.getType() == type && reservationService.isAvailable(vehicle, start, end)) availableVehicles.add(vehicle);
        }
        return availableVehicles;
    }
}
