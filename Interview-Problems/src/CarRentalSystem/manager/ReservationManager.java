package CarRentalSystem.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import CarRentalSystem.entities.Customer;
import CarRentalSystem.entities.Reservation;
import CarRentalSystem.entities.Vehicle;
import CarRentalSystem.enums.ReservationStatus;
import CarRentalSystem.enums.VehicleStatus;

// ======================= RESERVATION MANAGER =======================
// Keeps all reservations and checks availability.
// Date ranges live in Reservation, NOT in Vehicle.
public class ReservationManager {
    private final Map<Vehicle, List<Reservation>> reservationsByVehicle = new ConcurrentHashMap<>();
    private final Map<String, Reservation> reservationsById = new ConcurrentHashMap<>();
    private int counter = 1;
 
    // Two ranges overlap if newStart < existingEnd AND newEnd > existingStart
    public  boolean isAvailable(Vehicle vehicle,LocalDate start,LocalDate end) {
        synchronized (vehicle) {
            List<Reservation> reservations = reservationsByVehicle.get(vehicle);
            if (reservations == null) {
                return true;
            }
            for (Reservation reservation : reservations) {
                if (reservation.getStatus() == ReservationStatus.CANCELLED || reservation.getStatus() == ReservationStatus.COMPLETED) {
                    continue;
                }
                // Date ranges are [start, end).
                boolean overlaps = start.isBefore(reservation.getEndDate()) && end.isAfter(reservation.getStartDate());
                if (overlaps) {
                    return false;
                }
            }
            return true;
        }
    }
 
    // synchronized so two users can't book the same car at the same time
    public synchronized Reservation reserve(Customer customer, Vehicle vehicle, LocalDate start, LocalDate end) {
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        synchronized (vehicle) {
            if (vehicle.getStatus() == VehicleStatus.MAINTENANCE) {
                throw new IllegalStateException("Vehicle is under maintenance");
            }
            // Availability check and insertion use the SAME lock.
            if (!isAvailable(vehicle, start, end)) {
                throw new IllegalStateException("Vehicle not available for these dates");
            }
            String reservationId = "R" + (++counter);
            Reservation reservation = new Reservation("R" + (++counter),customer,vehicle,start,end);
            reservationsByVehicle.computeIfAbsent(vehicle, v -> new ArrayList<>()).add(reservation);
            reservationsById.put(reservationId, reservation);
            return reservation;
        }
    }
 
    public Reservation get(String reservationId) {
        Reservation r = reservationsById.get(reservationId);
        if (r == null) throw new IllegalArgumentException("Reservation not found");
        return r;
    }
}
