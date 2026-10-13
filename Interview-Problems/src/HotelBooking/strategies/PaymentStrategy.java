package HotelBooking.strategies;

import HotelBooking.entities.Payment;

public interface PaymentStrategy {
    Payment pay(double amount);
}
