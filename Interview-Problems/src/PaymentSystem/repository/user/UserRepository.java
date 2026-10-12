package PaymentSystem.repository.user;

import java.util.Optional;

import PaymentSystem.entities.*;

public interface UserRepository {
    void save(User user);
    Optional<User> findById(String userId);
}