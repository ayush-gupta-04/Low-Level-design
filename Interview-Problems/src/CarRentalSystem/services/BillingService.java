package CarRentalSystem.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import CarRentalSystem.entities.Bill;
import CarRentalSystem.entities.Reservation;
import CarRentalSystem.entities.Vehicle;
import CarRentalSystem.strategies.PricingStrategy;

/** Calculates rental charges and formats the bill for display. */
public class BillingService {
    private static final double LATE_FEE_MULTIPLIER = 1.5;
    private PricingStrategy pricingStrategy;

    public BillingService(PricingStrategy pricingStrategy){
        this.pricingStrategy = pricingStrategy;
    }

    public Bill generateBill(Reservation reservation, LocalDate returnDate) {
        Vehicle vehicle = reservation.getVehicle();
        double rentalAmount = pricingStrategy.calculate(vehicle, reservation.getStartDate(), reservation.getEndDate());
        double lateFee = 0;
        if (returnDate.isAfter(reservation.getEndDate())) {
            long lateDays = ChronoUnit.DAYS.between(reservation.getEndDate(), returnDate);
            lateFee = lateDays * vehicle.getPricePerDay() * LATE_FEE_MULTIPLIER;
        }

        return new Bill(reservation,rentalAmount, lateFee);
    }

    public void printBill(Bill bill) {
        if (bill == null) {
            return;
        }

        System.out.println("------ BILL ------");
        System.out.println("Customer : " + bill.getReservation().getCustomer().getName());
        System.out.println("Vehicle  : " + bill.getReservation().getVehicle());
        System.out.println("Rental   : Rs." + bill.getRentalAmount());
        System.out.println("Late fee : Rs." + bill.getLateFee());
        System.out.println("TOTAL    : Rs." + bill.getTotal());
        System.out.println("------------------");
    }
}
