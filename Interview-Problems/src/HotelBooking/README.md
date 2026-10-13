## Hotel Booking System — Core LLD Requirements

1. Hotel Management
- The system should maintain hotels with a unique ID, name, address, and location.
- Each hotel should contain multiple rooms.
2. Room Management
- Each room should have a unique room ID or room number within its hotel.
- Each room should have a room type, such as Single, Double, or Suite.
- Each room should have a price per night and a capacity.
- The system should maintain room availability for different dates.
3. Guest Management
- The system should maintain guests with a unique ID, name, and contact details.
- A guest should be able to view their bookings.
4. Hotel Search
- A guest should be able to search for hotels by location.
- The guest should provide check-in date, check-out date, and number of guests.
- The system should display hotels with available rooms matching the search criteria.
5. Room Availability
- The system should check whether a room is available for the requested date range.
- A room should not be booked by multiple guests for overlapping dates.
6. Booking Management
- A guest should be able to book an available room for a specified date range.
- Each booking should have a unique booking ID, guest, room, check-in date, check-out date, total price, and booking status.
- The system should support booking statuses such as CONFIRMED and CANCELLED.
- The system should calculate the total booking price based on the room's price per night and the number of nights.
7. Payment Management
- A guest should be able to pay for a booking.
- The system should maintain payment details, including payment ID, amount, payment method, and payment status.
- A booking should be confirmed only after successful payment, assuming the system uses a pay-before-confirmation flow.
8. Booking Cancellation
- A guest should be able to cancel an eligible booking.
- The system should update the booking status and make the room available for the cancelled dates.