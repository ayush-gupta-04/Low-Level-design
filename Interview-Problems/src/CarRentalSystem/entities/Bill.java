package CarRentalSystem.entities;

public class Bill {
    private final Reservation reservation;
    private final double rentalAmount;
    private final double lateFee;
 
    public Bill(Reservation reservation, double rentalAmount, double lateFee) {
        this.reservation = reservation;
        this.rentalAmount = rentalAmount;
        this.lateFee = lateFee;
    }
 
    public double getTotal() { return rentalAmount + lateFee; }
 
    public void print() {
        System.out.println("------ BILL ------");
        System.out.println("Customer : " + reservation.getCustomer().getName());
        System.out.println("Vehicle  : " + reservation.getVehicle());
        System.out.println("Rental   : Rs." + rentalAmount);
        System.out.println("Late fee : Rs." + lateFee);
        System.out.println("TOTAL    : Rs." + getTotal());
        System.out.println("------------------");
    }
}