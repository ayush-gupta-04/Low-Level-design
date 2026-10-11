package entities;

import enums.SeatType;

/**
 * Represents a physical seat on a screen.
 * Availability is not stored here because the same physical seat can be
 * available for one show and booked for another show.
 */
public class Seat {
    private final String id;
    private final int row;
    private final int col;
    private final SeatType type;

    public Seat(String id, int row, int col, SeatType type) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.type = type;
    }

    public String getId() { return id; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public SeatType getType() { return type; }
}
