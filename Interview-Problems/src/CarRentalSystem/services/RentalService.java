package CarRentalSystem.services;

import java.time.LocalDate;
import java.util.Optional;

import CarRentalSystem.entities.Bill;
import CarRentalSystem.entities.Payment;
import CarRentalSystem.entities.Reservation;
import CarRentalSystem.entities.Vehicle;
import CarRentalSystem.enums.PaymentStatus;
import CarRentalSystem.enums.ReservationStatus;
import CarRentalSystem.enums.VehicleStatus;
import CarRentalSystem.strategies.PaymentStrategy;

/** Coordinates the pickup and return lifecycle of a rental. */
public class RentalService {
    private final ReservationService reservationService;
    private final BillingService billingService;

    public RentalService(ReservationService reservationService, BillingService billingService) {
        this.reservationService = reservationService;
        this.billingService = billingService;
    }

    public boolean pickup(String reservationId) {
        Reservation reservation = reservationService.get(reservationId);
        if (reservation == null) {
            return false;
        }

        Vehicle vehicle = reservation.getVehicle();

        // vehicle level synchronisation is IMP.
        // suppose we have ThreadA (for reservation A) and ThreadB (for reservation B) for same vehicle.
        // both will check available vehicle and create their reservation as ongoing.
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

    public Optional<Bill> returnVehicle(String reservationId, LocalDate returnDate, PaymentStrategy paymentStrategy) {
        
        Reservation reservation = reservationService.get(reservationId);
        if (reservation == null) {
            return Optional.empty();
        }

        Vehicle vehicle = reservation.getVehicle();
        Bill bill;

        // vehicle level synchronisation is IMP.
        // suppose we have ThreadA (for reservation A) and ThreadB (for reservation B) for same vehicle.
        // both will check reservation as ongoing & generate 2 bills for same reservation.
        
        // do not have payment inside this lock.
        // TODO : Ideally a timer should unlock the the RETURN_PROCESSING if it is still RETURN_PROCESSING.
        synchronized (vehicle) {
            if (reservation.getStatus() != ReservationStatus.ONGOING || returnDate.isBefore(reservation.getStartDate())) {
                return Optional.empty();
            }

            bill = billingService.generateBill(reservation, returnDate);
            reservation.setStatus(ReservationStatus.RETURN_PROCESSING);
        }


        // calc due amount.
        // do the payment
        double amountDue = Math.max(0.0, bill.getTotal() - reservation.getPrepaidRentalAmount());
        if (amountDue > 0) {
            Payment payment = paymentStrategy.pay(amountDue);

            if (payment == null || payment.getStatus() != PaymentStatus.SUCCESS) {
                restoreOngoingStatus(reservation);
                return Optional.empty();
            }
        }

        // Atomically finalize the return.
        synchronized (vehicle) {
            if (reservation.getStatus() != ReservationStatus.RETURN_PROCESSING) {
                return Optional.empty();
            }
            reservation.setStatus(ReservationStatus.COMPLETED);
            vehicle.setStatus(VehicleStatus.AVAILABLE);
        }

        return Optional.of(bill);
    }

    private void restoreOngoingStatus(Reservation reservation) {
        Vehicle vehicle = reservation.getVehicle();
        synchronized (vehicle) {
            if (reservation.getStatus() == ReservationStatus.RETURN_PROCESSING) {
                reservation.setStatus(ReservationStatus.ONGOING);
            }
        }
    }
}
