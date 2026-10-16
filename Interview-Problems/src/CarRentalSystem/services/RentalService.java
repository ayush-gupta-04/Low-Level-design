package CarRentalSystem.services;

import java.time.LocalDate;
import java.util.Optional;

import CarRentalSystem.entities.Bill;
import CarRentalSystem.entities.Reservation;
import CarRentalSystem.entities.Vehicle;
import CarRentalSystem.enums.ReservationStatus;
import CarRentalSystem.enums.VehicleStatus;
import CarRentalSystem.strategies.PaymentMethod;

/** Coordinates the pickup and return lifecycle of a rental. */
public class RentalService {
    private final ReservationService reservationService;
    private final BillingService billingService;

    public RentalService(ReservationService reservationService, BillingService billingService) {

        this.reservationService = reservationService;
        this.billingService = billingService;
    }

    public boolean pickup(String reservationId) {
        Optional<Reservation> maybeReservation = reservationService.get(reservationId);
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

    public Optional<Bill> returnVehicle(String reservationId, LocalDate returnDate, PaymentMethod paymentMethod) {
        if (returnDate == null || paymentMethod == null) {
            return Optional.empty();
        }

        Optional<Reservation> maybeReservation = reservationService.get(reservationId);
        if (!maybeReservation.isPresent()) {
            return Optional.empty();
        }

        Reservation reservation = maybeReservation.get();
        Vehicle vehicle = reservation.getVehicle();

        synchronized (vehicle) {
            if (reservation.getStatus() != ReservationStatus.ONGOING || returnDate.isBefore(reservation.getStartDate())) {
                return Optional.empty();
            }

            Bill bill = billingService.generateBill(reservation, returnDate);
            boolean paid = paymentMethod.pay(bill.getTotal());
            if (!paid) {
                return Optional.empty();
            }

            reservation.setStatus(ReservationStatus.COMPLETED);
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            billingService.printBill(bill);
            return Optional.of(bill);
        }
    }
}
