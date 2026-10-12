package ParkingLot.Vehicle;

public enum VehicleSize {
    SMALL(1),
    MEDIUM(2),
    LARGE(3);

    private final int rank;

    VehicleSize(int rank) {
        this.rank = rank;
    }

    public int getRank(){
        return this.rank;
    }
    public boolean canAccommodate(VehicleSize requiredSize) {
        return this.rank >= requiredSize.rank;
    }
}