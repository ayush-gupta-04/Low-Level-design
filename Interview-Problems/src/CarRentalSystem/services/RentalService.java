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

    // vehicle level synchronisation is IMP.
    // suppose we have ThreadA (for reservation A) and ThreadB (for reservation B) for same vehicle.
    // both will check available vehicle and create their reservation as ongoing.
    public boolean pickup(String reservationId) {
        Reservation reservation = reservationService.get(reservationId);
        if (reservation == null) {
            return false;
        }
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

    public Optional<Bill> returnVehicle(String reservationId, LocalDate returnDate, PaymentStrategy paymentStrategy) {
        
        Reservation reservation = reservationService.get(reservationId);
        if (reservation == null) return Optional.empty();

        Vehicle vehicle = reservation.getVehicle();

        // Step 1: Atomically claim the return operation.
        if (!beginReturnProcessing(reservation, returnDate)) {
            return Optional.empty();
        }

        // Step 2: Generate the bill after claiming the return.
        // No other thread can begin another return for this reservation.
        Bill bill = billingService.generateBill(reservation, returnDate);
        

        // Step 4: Process payment OUTSIDE the vehicle lock.
        double amountDue = Math.max(0.0, bill.getTotal() - reservation.getPrepaidRentalAmount());
        if (amountDue > 0) {
            try {
                Payment payment = paymentStrategy.pay(amountDue);
                
                if (payment != null && payment.getStatus() == PaymentStatus.FAILED) {
                    reservation.setStatus(ReservationStatus.ONGOING);
                    return Optional.empty();
                }

                // If payment is PENDING or null or UNKNOWN: 
                //  - For now: leave ticket in PROCESSING & return false;
                //  - Reconcilation Process will it resolve it later in background.

            } catch (Exception e) {
                // Reason of failure can be anything.
                // We must check the payment status, and act accordingly.
                // There can be three cases .. SUCCESS, FAILURE, (PENDING or null or UNKNOWN)
                return Optional.empty();
            }
        }


        // finalize the return.
        reservation.setStatus(ReservationStatus.COMPLETED);
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        System.out.println("Return Confirmed : " + reservation.getId());
        return Optional.of(bill);
    }



    // ------------ Helper Functions ---------------

    private boolean beginReturnProcessing(Reservation reservation, LocalDate returnDate) {
        Vehicle vehicle = reservation.getVehicle();
        synchronized (vehicle) {
            if (reservation.getStatus() != ReservationStatus.ONGOING || returnDate.isBefore(reservation.getStartDate())) {
                return false;
            }
            reservation.setStatus(ReservationStatus.RETURN_PROCESSING);
            return true;
        }
    }
}
