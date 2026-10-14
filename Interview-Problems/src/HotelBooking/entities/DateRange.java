package HotelBooking.entities;

import java.time.LocalDate;

/** A stay from checkIn (inclusive) to checkOut (exclusive). */
public record DateRange(LocalDate checkIn, LocalDate checkOut) {

    public long nights() {
        return checkOut.toEpochDay() - checkIn.toEpochDay();
    }

    // Checkout day is free, so one guest can check in the day another checks out.
    public boolean overlaps(DateRange other) {
        return checkIn.isBefore(other.checkOut) && checkOut.isAfter(other.checkIn);
    }
}
