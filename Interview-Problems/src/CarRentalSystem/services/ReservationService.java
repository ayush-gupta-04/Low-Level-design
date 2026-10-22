package CarRentalSystem.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import CarRentalSystem.entities.Customer;
import CarRentalSystem.entities.Payment;
import CarRentalSystem.entities.Reservation;
import CarRentalSystem.entities.Vehicle;
import CarRentalSystem.enums.PaymentStatus;
import CarRentalSystem.enums.ReservationStatus;
import CarRentalSystem.enums.VehicleStatus;
import CarRentalSystem.strategies.PaymentStrategy;
import CarRentalSystem.strategies.PricingStrategy;


public class ReservationService {
    private final Map<Vehicle, List<Reservation>> reservationsByVehicle = new ConcurrentHashMap<>();
    private final Map<String, Reservation> reservationsById = new ConcurrentHashMap<>();
    private PricingStrategy pricingStrategy;

    public ReservationService(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }
    

    // main methods
    // create reservation & cancel reservation
    public Optional<Reservation> createReservation(Customer customer, Vehicle vehicle, LocalDate start, LocalDate end, PaymentStrategy paymentStrategy) {

        // create a pending reservation.
        // set the prepaid amount in reservation.
        double estimatedAmount = pricingStrategy.calculate(vehicle, start, end);
        Reservation reservation = new Reservation(customer, vehicle, start, end);
        reservation.setPrepaidRentalAmount(estimatedAmount);

        // TODO : Ideally a timer should unlock the the pending_payment if it is still pending.
        // Atomically create a temporary reservation hold.
        // - Thread A : Acquires the vehicle lock -> checks availability -> inserts a pending reservation
        // - Thread B : Waits for the same vehicle lock -> checks availability after A releases it -> sees A's hold and rejects the overlapping booking.
        synchronized (vehicle) {
            if (!isAvailable(vehicle, start, end)) {
                return Optional.empty();
            }
            reservation.setStatus(ReservationStatus.PENDING_PAYMENT);
            reservationsByVehicle.computeIfAbsent(vehicle, key -> new ArrayList<>()).add(reservation);
            reservationsById.put(reservation.getId(), reservation);
        }

        // do payment
        // must be under try & catch
        Payment payment = paymentStrategy.pay(estimatedAmount);

        if(payment==null || payment.getStatus() != PaymentStatus.SUCCESS){
            cancelPendingReservation(reservation);
            return Optional.empty();
        }

        // Confirm the reservation under the vehicle lock.
        synchronized(vehicle){
            if(reservation.getStatus() != ReservationStatus.PENDING_PAYMENT){
                return Optional.empty();
            }
            reservation.setStatus(ReservationStatus.CONFIRMED);
        }

        System.out.println("Reservation " + reservation.getId() + " confirmed. Amount paid: Rs." + estimatedAmount);

        return Optional.of(reservation);
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

    private void cancelPendingReservation(Reservation reservation){
        Vehicle vehicle = reservation.getVehicle();
        synchronized (vehicle) {
            if (reservation.getStatus() == ReservationStatus.PENDING_PAYMENT) {
                reservation.setStatus(ReservationStatus.CANCELLED);
            }
        }
    }
    // ---------
    



    // Helper Functions
    public Reservation get(String resvId){
        return reservationsById.get(resvId);
    }

    public boolean isAvailable(Vehicle vehicle, LocalDate start, LocalDate end) {

        synchronized (vehicle) {
            if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
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
                if (start.isBefore(reservation.getEndDate()) && end.isAfter(reservation.getStartDate())) {
                    return false;
                }
            }
            return true;
        }
    }
}



// Question : 
// Consider the flow:
// 1. A customer obtains a pending hold.
// 2. Payment processing begins.
// 3. The hold expires and another customer books the same date range.
// 4. The first customer's payment succeeds.
// 5. The first customer attempts to confirm the expired reservation.

// Answer : 
// 1. Create temporary hold ... Reservation = PENDING_PAYMENT
// 2. Process payment outside the lock ... Payment gateway returns SUCCESS
// 3. Reacquire vehicle lock ... Check reservation status and hold expiry
//    - Hold is valid ... Confirm reservation
//    - Hold expired ... Refund or reverse payment