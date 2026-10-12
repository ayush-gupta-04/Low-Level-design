package PaymentSystem.repository.user;


import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import PaymentSystem.entities.*;

public final class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> users = new ConcurrentHashMap<>();

    @Override
    public void save(User user) {
        User existing = users.putIfAbsent(user.getUserId(), user);
        if (existing != null) {
            throw new IllegalArgumentException("User ID already exists");
        }
    }

    @Override
    public Optional<User> findById(String userId) {
        return Optional.ofNullable(users.get(userId));
    }
}