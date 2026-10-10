package enums;

public enum SeatType {
    SILVER(100.0),
    PLATINUM(200.0),
    GOLD(150.0);

    public final double price;
    SeatType(double price){
        this.price = price;
    }
    public double getPrice(){
        return this.price;
    }
}