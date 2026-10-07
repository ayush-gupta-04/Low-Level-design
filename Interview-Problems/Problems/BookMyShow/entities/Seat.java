package entities;

import enums.SeatStatus;
import enums.SeatType;

public class Seat {
    private String id;
    private int row;
    private int col;
    private SeatStatus status;
    private SeatType type;

    public Seat(String id, int row, int col, SeatType type){
        this.id = id;
        this.row = row;
        this.col = col;
        this.type = type;
        this.status = SeatStatus.AVAILABLE;
    }

    public String getId() { return id; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public SeatStatus getStatus() { return status; }
    public void setSeatStatus(SeatStatus status) {this.status = status;}
    public SeatType getType() { return type; }
}