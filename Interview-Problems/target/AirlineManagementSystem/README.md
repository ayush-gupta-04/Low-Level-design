# Requirements
1. Flight Management
- The system should maintain flights with flight number, source, destination, departure time, arrival time, and duration.
- Each flight should have an aircraft assigned to it.
- An aircraft should contain seats with seat numbers, classes (Economy, Business), and seat types (Window, Aisle, Middle).
- The system should support searching flights by source, destination, and travel date.
2. Seat Management
- Users should be able to view all seats and their availability for a particular flight.
- Users should be able to select one or multiple seats.
- The system should display seat status: AVAILABLE, HELD, or BOOKED.
- Seat availability should be maintained separately for each flight instance, i.e., the same seat on different dates can be booked independently.
3. Booking Management
- Users should be able to book one or multiple available seats.
- The system should generate a unique booking ID.
- The booking should contain user details, flight details, selected seats, total fare, and booking status.
- Users should be able to view their booking details.
- The system should support booking statuses such as PENDING, CONFIRMED, CANCELLED, and EXPIRED.
4. Concurrency Control — Most Important
- Multiple users may try to book the same seat simultaneously.
- A seat must never be confirmed for more than one booking on the same flight instance.
- The system should atomically check seat availability and reserve the selected seats.
- If two users request the same seat, only one should successfully reserve it.
- If a user selects multiple seats, the system should reserve all of them together or none of them.
- Temporary seat holds should expire after a fixed duration if the user does not complete the booking.
5. Payment Management
- Users should be able to pay for a booking.
- A booking should be confirmed only after successful payment.
- If payment fails or the hold expires, the seats should be released.
6. Cancellation Management
- Users should be able to cancel their confirmed bookings.
- Cancelled seats should become available again.
- The system should calculate the refund.