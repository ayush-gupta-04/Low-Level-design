package PaymentSystem.repository.payment;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import PaymentSystem.entities.*;


public final class InMemoryPaymentRepository implements PaymentRepository {

    private final Map<String, Payment> payments = new ConcurrentHashMap<>();

    @Override
    public void save(Payment payment) {
        payments.put(payment.getPaymentId(), payment);
    }

    @Override
    public Optional<Payment> findById(String paymentId) {
        return Optional.ofNullable(payments.get(paymentId));
    }

    @Override
    public List<Payment> findByUserId(String userId) {
        return payments.values().stream()
                .filter(p -> p.getUserId().equals(userId))
                .sorted(Comparator.comparing(Payment::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }
}