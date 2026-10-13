package FlightBooking.enums;

public enum SeatClass {
    ECONOMY(5000.0),
    BUSINESS(12000.0),
    FIRST_CLASS(25000.0);

    private final double basePrice;

    SeatClass(double basePrice) {
        this.basePrice = basePrice;
    }

    public double getBasePrice() {
        return basePrice;
    }
}
