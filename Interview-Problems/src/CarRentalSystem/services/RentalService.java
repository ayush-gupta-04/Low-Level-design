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
        if (returnDate == null || paymentStrategy == null) {
            return Optional.empty();
        }

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
        synchronized (vehicle) {
            if (reservation.getStatus() != ReservationStatus.ONGOING || returnDate.isBefore(reservation.getStartDate())) {
                return Optional.empty();
            }

            bill = billingService.generateBill(reservation, returnDate);

            reservation.setStatus(ReservationStatus.COMPLETED);
            vehicle.setStatus(VehicleStatus.AVAILABLE);
        }

        // we make a payment object so payment can be tracked and retries with idempotency.
        // we don't have idempotency here.
        // we can introduce paymentService which handles paymentAttempt creation and Payment Creation.
        Payment payment = paymentStrategy.pay(bill.getBillId(), bill.getTotal() + bill.getLateFee());
        if(payment.getStatus() == PaymentStatus.SUCCESS){
            System.out.println("Payment Successfull");
        }

        return Optional.of(bill);
    }
}
