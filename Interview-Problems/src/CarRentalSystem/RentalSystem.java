package CarRentalSystem;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import CarRentalSystem.entities.Bill;
import CarRentalSystem.entities.Customer;
import CarRentalSystem.entities.Reservation;
import CarRentalSystem.entities.Vehicle;
import CarRentalSystem.enums.ReservationStatus;
import CarRentalSystem.enums.VehicleStatus;
import CarRentalSystem.enums.VehicleType;
import CarRentalSystem.manager.ReservationManager;
import CarRentalSystem.strategies.PaymentMethod;
import CarRentalSystem.strategies.PricingStrategy;
import CarRentalSystem.strategies.pricing.DailyPricing;

// Main entry point for the car rental system.
// Invalid or unsuccessful operations return false, an empty list, or Optional.empty().
public class RentalSystem {
    private static final double LATE_FEE_MULTIPLIER = 1.5;

    private final Map<String, Vehicle> vehicles = new ConcurrentHashMap<>();
    private final ReservationManager reservationManager = new ReservationManager();
    private final PricingStrategy pricingStrategy = new DailyPricing();

    // ---- Vehicle management ----
    public boolean addVehicle(Vehicle vehicle) {
        if (vehicle == null || vehicle.getId() == null || vehicle.getId().trim().isEmpty()) {
            return false;
        }
        return vehicles.putIfAbsent(vehicle.getId(), vehicle) == null;
    }

    public boolean removeVehicle(String vehicleId) {
        if (vehicleId == null || vehicleId.trim().isEmpty()) {
            return false;
        }
        return vehicles.remove(vehicleId) != null;
    }

    // ---- Search: vehicles of a type available for the given date range ----
    public List<Vehicle> search(VehicleType type, LocalDate start, LocalDate end) {
        List<Vehicle> result = new ArrayList<>();
        if (type == null || start == null || end == null || !end.isAfter(start)) {
            return result;
        }

        for (Vehicle vehicle : vehicles.values()) {
            if (vehicle.getType() == type && reservationManager.isAvailable(vehicle, start, end)) {
                result.add(vehicle);
            }
        }
        return result;
    }

    // ---- Booking ----
    public Optional<Reservation> reserve(Customer customer, String vehicleId, LocalDate start, LocalDate end) {
        if (customer == null || vehicleId == null || vehicleId.trim().isEmpty() || start == null || end == null || !end.isAfter(start)) {
            return Optional.empty();
        }

        Vehicle vehicle = vehicles.get(vehicleId);
        if (vehicle == null) return Optional.empty();

        Optional<Reservation> reservation = reservationManager.reserve(customer, vehicle, start, end);
        if(reservation.isPresent()){
            System.out.println("Reservation " + reservation.get().getId() + " confirmed for " + customer.getName() + " : " + vehicle + " (" + start + " to " + end + ")");
            return reservation;
        }
        return Optional.empty();
    }

    public boolean cancel(String reservationId) {
        Optional<Reservation> maybeReservation = reservationManager.get(reservationId);
        if (!maybeReservation.isPresent()) {
            return false;
        }

        Reservation reservation = maybeReservation.get();

        Vehicle vehicle = reservation.getVehicle();
        synchronized (vehicle) {
            if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
                return false;
            }
            reservation.setStatus(ReservationStatus.CANCELLED);
            return true;
        }
    }

    // ---- Pickup: rental starts ----
    public boolean pickup(String reservationId) {
        Optional<Reservation> maybeReservation = reservationManager.get(reservationId);
        if (!maybeReservation.isPresent()) {
            return false;
        }

        Reservation reservation = maybeReservation.get();
        Vehicle vehicle = reservation.getVehicle();

        synchronized (vehicle) {
            if (reservation.getStatus() != ReservationStatus.CONFIRMED || vehicle.getStatus() != VehicleStatus.AVAILABLE) {
                return false;
            }
            reservation.setStatus(ReservationStatus.ONGOING);
            vehicle.setStatus(VehicleStatus.RENTED);
        }

        System.out.println("Vehicle picked up for reservation " + reservationId);
        return true;
    }

    // ---- Return: rental ends, bill is generated, and payment is attempted ----
    public Optional<Bill> returnVehicle(String reservationId, LocalDate returnDate, PaymentMethod paymentMethod) {
        if (returnDate == null || paymentMethod == null) {
            return Optional.empty();
        }

        Optional<Reservation> maybeReservation = reservationManager.get(reservationId);
        if(maybeReservation.isEmpty()){
            return Optional.empty();
        }

        Reservation reservation = maybeReservation.get();
        Vehicle vehicle = reservation.getVehicle();
        
        synchronized (vehicle) {
            if (reservation.getStatus() != ReservationStatus.ONGOING || returnDate.isBefore(reservation.getStartDate())) {
                return Optional.empty();
            }

            long plannedDays = Math.max(1, ChronoUnit.DAYS.between(reservation.getStartDate(), reservation.getEndDate()));
            double rentalAmount = pricingStrategy.calculate(vehicle, plannedDays);

            double lateFee = 0;
            if (returnDate.isAfter(reservation.getEndDate())) {
                long lateDays = ChronoUnit.DAYS.between(reservation.getEndDate(), returnDate);
                lateFee = lateDays * vehicle.getPricePerDay() * LATE_FEE_MULTIPLIER;
            }

            Bill bill = new Bill(reservation, rentalAmount, lateFee);
            boolean paid;
            try {
                paid = paymentMethod.pay(bill.getTotal());
            } catch (RuntimeException paymentError) {
                // Treat a payment-strategy failure like an unsuccessful payment.
                return Optional.empty();
            }

            if (!paid) {
                return Optional.empty();
            }

            reservation.setStatus(ReservationStatus.COMPLETED);
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            bill.print();
            return Optional.of(bill);
        }
    }
}
