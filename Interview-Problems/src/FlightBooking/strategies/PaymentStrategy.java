package FlightBooking.strategies;

import FlightBooking.entities.Payment;

public interface PaymentStrategy {
    /** Always returns a Payment; check getStatus() for SUCCESS / FAILED. */
    Payment pay(double amount);
}
