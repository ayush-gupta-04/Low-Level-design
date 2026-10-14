package CarRentalSystem;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

// ======================= RENTAL SYSTEM (main entry point) =======================
public class RentalSystem {
    private static final double LATE_FEE_MULTIPLIER = 1.5; // late days cost 1.5x
 
    private final Map<String, Vehicle> vehicles = new HashMap<>();
    private final ReservationManager reservationManager = new ReservationManager();
    private final PricingStrategy pricingStrategy = new DailyPricing();
 
    // ---- Vehicle management ----
    public void addVehicle(Vehicle v) { vehicles.put(v.getId(), v); }
 
    public void removeVehicle(String vehicleId) { vehicles.remove(vehicleId); }
 
    // ---- Search: vehicles of a type that are free for the date range ----
    public List<Vehicle> search(VehicleType type, LocalDate start, LocalDate end) {
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle v : vehicles.values()) {
            if (v.getType() == type
                    && v.getStatus() != VehicleStatus.MAINTENANCE
                    && reservationManager.isAvailable(v, start, end)) {
                result.add(v);
            }
        }
        return result;
    }
 
    // ---- Booking ----
    public Reservation reserve(Customer customer, String vehicleId, LocalDate start, LocalDate end) {
        Vehicle vehicle = vehicles.get(vehicleId);
        if (vehicle == null) throw new IllegalArgumentException("Vehicle not found");

        Reservation r = reservationManager.reserve(customer, vehicle, start, end);
        System.out.println("Reservation " + r.getId() + " confirmed for " + customer.getName() + " : " + vehicle + " (" + start + " to " + end + ")");
        return r;
    }
 
    public void cancel(String reservationId) {
        Reservation reservation = reservationManager.get(reservationId);
        Vehicle vehicle = reservation.getVehicle();

        synchronized (vehicle) {
            if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
                throw new IllegalStateException("Only CONFIRMED reservations can be cancelled");
            }
            reservation.setStatus(ReservationStatus.CANCELLED);
        }
    }
 
    // ---- Pickup: rental starts ----
    public void pickup(String reservationId) {
        Reservation r = reservationManager.get(reservationId);
        if (r.getStatus() != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("Reservation is not in CONFIRMED state");
        }
        r.setStatus(ReservationStatus.ONGOING);
        r.getVehicle().setStatus(VehicleStatus.RENTED);
        System.out.println("Vehicle picked up for reservation " + reservationId);
    }
 
    // ---- Return: rental ends, bill is generated, payment is made ----
    public Bill returnVehicle(String reservationId, LocalDate returnDate, PaymentMethod paymentMethod) {
        Reservation r = reservationManager.get(reservationId);
        if (r.getStatus() != ReservationStatus.ONGOING) {
            throw new IllegalStateException("Reservation is not ONGOING");
        }
 
        Vehicle vehicle = r.getVehicle();
 
        // Base rental amount (planned days, minimum 1 day)
        long plannedDays = Math.max(1, ChronoUnit.DAYS.between(r.getStartDate(), r.getEndDate()));
        double rentalAmount = pricingStrategy.calculate(vehicle, plannedDays);
 
        // Late fee if returned after the planned end date
        double lateFee = 0;
        if (returnDate.isAfter(r.getEndDate())) {
            long lateDays = ChronoUnit.DAYS.between(r.getEndDate(), returnDate);
            lateFee = lateDays * vehicle.getPricePerDay() * LATE_FEE_MULTIPLIER;
        }
 
        Bill bill = new Bill(r, rentalAmount, lateFee);
        bill.print();
 
        boolean paid = paymentMethod.pay(bill.getTotal());
        if (!paid) {
            throw new IllegalStateException("Payment failed. Please retry.");
        }
 
        r.setStatus(ReservationStatus.COMPLETED);
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        return bill;
    }
}
