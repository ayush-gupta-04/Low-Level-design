package CarRentalSystem.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import CarRentalSystem.entities.Customer;
import CarRentalSystem.entities.Reservation;
import CarRentalSystem.entities.Vehicle;
import CarRentalSystem.enums.ReservationStatus;
import CarRentalSystem.enums.VehicleStatus;

/** Owns reservations, date-range availability checks, booking, and cancellation. */
public class ReservationService {
    private final Map<Vehicle, List<Reservation>> reservationsByVehicle = new ConcurrentHashMap<>();
    private final Map<String, Reservation> reservationsById = new ConcurrentHashMap<>();

    public ReservationService() {

    }

    // main methods
    // create reservation & cancel reservation
    public Optional<Reservation> createReservation(Customer customer, Vehicle vehicle, LocalDate start, LocalDate end) {
        synchronized (vehicle) {
            if (vehicle.getStatus() == VehicleStatus.MAINTENANCE || !isAvailable(vehicle, start, end)) {
                return Optional.empty();
            }

            Reservation reservation = new Reservation(customer, vehicle, start, end);
            reservationsByVehicle.computeIfAbsent(vehicle, key -> new ArrayList<>()).add(reservation);
            reservationsById.put(reservation.getId(), reservation);
            System.out.println("Reservation " + reservation.getId() + " confirmed for " + customer.getName() + " : " + vehicle + " (" + start + " to " + end + ")");
            return Optional.of(reservation);
        }
    }
    public boolean cancelReservation(String reservationId) {
        Reservation reservation = reservationsById.get(reservationId);
        if (reservation==null) {
            return false;
        }
        Vehicle vehicle = reservation.getVehicle();
        synchronized (vehicle) {
            if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
                return false;
            }
            reservation.setStatus(ReservationStatus.CANCELLED);
            return true;
        }
    }
    // ---------
    



    // Helper Function.
    /** Date ranges are [start, end); existing and requested ranges overlap if start < existingEnd and end > existingStart. */
    public boolean isAvailable(Vehicle vehicle, LocalDate start, LocalDate end) {

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
    public Optional<Reservation> get(String resvId){
        return Optional.ofNullable(reservationsById.get(resvId));
    }
}
