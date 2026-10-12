package PaymentSystem.repository.payment;

import java.util.List;
import java.util.Optional;

import PaymentSystem.entities.Payment;

public interface PaymentRepository {
    void save(Payment payment);
    Optional<Payment> findById(String paymentId);
    List<Payment> findByUserId(String userId);
}
