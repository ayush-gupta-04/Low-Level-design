package CarRentalSystem.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import CarRentalSystem.entities.Customer;
import CarRentalSystem.entities.Reservation;
import CarRentalSystem.entities.Vehicle;
import CarRentalSystem.enums.ReservationStatus;
import CarRentalSystem.enums.VehicleStatus;

// Keeps reservations and checks date-range availability. Date ranges live in
// Reservation, not Vehicle. Failed operations return false / Optional.empty().
public class ReservationManager {
    private final Map<Vehicle, List<Reservation>> reservationsByVehicle = new ConcurrentHashMap<>();
    private final Map<String, Reservation> reservationsById = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(0);

    // Date ranges are [start, end). They overlap iff start < existingEnd
    // and end > existingStart.
    public boolean isAvailable(Vehicle vehicle, LocalDate start, LocalDate end) {
        if (vehicle == null || start == null || end == null || !end.isAfter(start)) {
            return false;
        }

        synchronized (vehicle) {
            if (vehicle.getStatus() == VehicleStatus.MAINTENANCE) {
                return false;
            }

            List<Reservation> reservations = reservationsByVehicle.get(vehicle);
            if (reservations == null) {
                return true;
            }

            for (Reservation reservation : reservations) {
                if (reservation.getStatus() == ReservationStatus.CANCELLED || reservation.getStatus() == ReservationStatus.COMPLETED) {
                    continue;
                }

                boolean overlaps = start.isBefore(reservation.getEndDate()) && end.isAfter(reservation.getStartDate());
                if (overlaps) {
                    return false;
                }
            }
            return true;
        }
    }

    // Availability check and reservation insertion happen under the same vehicle lock.
    public Optional<Reservation> reserve(Customer customer, Vehicle vehicle, LocalDate start, LocalDate end) {
        if (customer == null || vehicle == null || start == null || end == null || !end.isAfter(start)) {
            return Optional.empty();
        }

        synchronized (vehicle) {
            if (vehicle.getStatus() == VehicleStatus.MAINTENANCE || !isAvailable(vehicle, start, end)) {
                return Optional.empty();
            }

            String reservationId = "R" + counter.incrementAndGet();
            Reservation reservation = new Reservation(reservationId, customer, vehicle, start, end);
            reservationsByVehicle.computeIfAbsent(vehicle, k -> new ArrayList<>()).add(reservation);
            reservationsById.put(reservationId, reservation);
            return Optional.of(reservation);
        }
    }

    public Optional<Reservation> get(String reservationId) {
        if (reservationId == null || reservationId.trim().isEmpty() || !reservationsById.containsKey(reservationId)) {
            return Optional.empty();
        }
        return Optional.of(reservationsById.get(reservationId));
    }
}
