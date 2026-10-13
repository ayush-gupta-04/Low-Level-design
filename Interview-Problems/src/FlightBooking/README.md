# Flight Booking System (LLD)

## Structure
```
FlightBooking/
├── Main.java                          demo of the whole flow
├── enums/       SeatClass, SeatPosition, SeatStatus, BookingStatus, PaymentStatus, PaymentMethod
├── entities/    Airport, Aircraft, Seat, Flight, FlightSeat, Passenger, Payment, Booking
├── strategies/  PricingStrategy  -> StandardPricingStrategy, PeakSeasonPricingStrategy
│                PaymentStrategy  -> CardPaymentStrategy, UpiPaymentStrategy
└── manager/     FlightBookingSystem (facade), FlightManager (search), SeatManager (seat states), BookingManager (book / cancel)
```

## Requirement -> where it lives
| Requirement | Class(es) |
|---|---|
| Airports (unique code) | `Airport` |
| Aircraft, capacity, seat layout | `Aircraft`, `Seat` (number, class, position) |
| Flights | `Flight` |
| Flight search (source, destination, date) | `FlightManager.searchFlights` |
| Passengers, many per booking | `Passenger`, `Booking` |
| Per-flight seat inventory, no double booking | `FlightSeat`, `SeatManager` |
| Fare by seat class | `SeatClass.basePrice` + `PricingStrategy` |
| Booking (CONFIRMED / CANCELLED) | `Booking`, `BookingManager` |
| Card / UPI payments | `PaymentStrategy` implementations |
| Cancellation + seat release | `BookingManager.cancelBooking`, `SeatManager.releaseBookedSeats` |

## Booking flow
lock seats (AVAILABLE -> LOCKED) -> calculate fare -> pay -> confirm seats (LOCKED -> BOOKED) -> create CONFIRMED booking.
If payment fails (or anything throws), the seats go back to AVAILABLE and no booking is created.

## Run
```
javac -d out $(find FlightBooking -name '*.java')
java -cp out FlightBooking.Main
```
(Run from the folder that contains `FlightBooking/`. Needs Java 16+.)
