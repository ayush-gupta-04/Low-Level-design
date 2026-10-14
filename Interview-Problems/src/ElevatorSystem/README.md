# Elevator System LLD: Final Core Requirements

**1. Elevator and Floor Management**
- Support multiple elevators serving a building with fixed minimum and maximum floors.
- Each elevator has an ID, current floor, direction (`UP`, `DOWN`, `IDLE`), status, and maximum capacity.
- Track the current passenger count and never exceed capacity.

**2. Request Handling**
- External request: a user on any valid floor requests an elevator with a direction (`UP` or `DOWN`).
- Internal request: a user inside an elevator selects a valid destination floor.
- Validate all requests, reject invalid floors, and ignore duplicates.

**3. Assignment and Scheduling**
- Assign each external request to the most suitable elevator, based on its current floor, direction, and availability.
- Prefer an elevator already moving toward the request in the same direction, or the nearest idle one, to minimize waiting time.
- Each elevator keeps its own pending stops and serves them in direction order (SCAN-style), avoiding unnecessary direction reversals.
- Keep the algorithm simple and deterministic, with a fixed tie-breaker such as the lowest elevator ID.

**4. Movement and Door Operation**
- Move one floor at a time, stay within floor limits, and never move while doors are open.
- Stop at floors with pending requests, open the doors, then close them before moving again.
- Reverse direction only when no further stops remain ahead, and become idle when no requests remain.

**5. Concurrency and Thread Safety**
- Handle concurrent requests from multiple threads.
- Protect shared state (request queues, elevator state, passenger count) from race conditions.
- Make assignment, movement, and capacity updates thread-safe, using Java locks or `synchronized` only where shared state needs it.