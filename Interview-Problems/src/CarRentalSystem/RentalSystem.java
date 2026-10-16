package CarRentalSystem;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import CarRentalSystem.entities.Bill;
import CarRentalSystem.entities.Customer;
import CarRentalSystem.entities.Reservation;
import CarRentalSystem.entities.Vehicle;
import CarRentalSystem.enums.VehicleType;
import CarRentalSystem.services.CustomerService;
import CarRentalSystem.services.RentalService;
import CarRentalSystem.services.ReservationService;
import CarRentalSystem.services.VehicleService;
import CarRentalSystem.strategies.PaymentMethod;

/** Facade: exposes the car-rental use cases and delegates the work to services. */
public class RentalSystem {
    private final VehicleService vehicleService;
    private final ReservationService reservationService;
    private final RentalService rentalService;
    private final CustomerService customerService;


    /** Allows services to be wired explicitly, for example in tests. */
    public RentalSystem(VehicleService vehicleService, ReservationService reservationService, RentalService rentalService, CustomerService customerService) {
        this.vehicleService = vehicleService;
        this.reservationService = reservationService;
        this.rentalService = rentalService;
        this.customerService = customerService;
    }

    public boolean addVehicle(Vehicle vehicle) {
        return vehicleService.addVehicle(vehicle);
    }

    public boolean removeVehicle(String vehicleId) {
        return vehicleService.removeVehicle(vehicleId);
    }
    public void addCustomer(Customer customer){
        customerService.addCustomer(customer);
    }
    public void removeCustomer(String customerId){
        customerService.removeCustomer(customerId);
    }

    public List<Vehicle> searchAvailableVehicles(VehicleType type, LocalDate start, LocalDate end) {
        return reservationService.searchAvailable(type, start, end);
    }

    public Optional<Reservation> createReservation(String customerId, String vehicleId, LocalDate start, LocalDate end) {
        Optional<Customer> customer = customerService.getCustomer(customerId);
        Optional<Vehicle> vehicle = vehicleService.getVehicle(vehicleId);
        if(vehicle.isEmpty() || customer.isEmpty()) return Optional.empty();

        return reservationService.createReservation(customer.get(), vehicle.get(), start, end);
    }

    public boolean cancelReservation(String reservationId) {
        return reservationService.cancelReservation(reservationId);
    }

    public boolean pickupVehicle(String reservationId) {
        return rentalService.pickup(reservationId);
    }

    public Optional<Bill> returnVehicle(String reservationId, LocalDate returnDate, PaymentMethod paymentMethod) {
        return rentalService.returnVehicle(reservationId, returnDate, paymentMethod);
    }
}
