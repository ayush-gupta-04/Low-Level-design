package PaymentSystem.service;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import PaymentSystem.entities.*;
import PaymentSystem.enums.PaymentAttemptStatus;
import PaymentSystem.enums.PaymentMethodType;
import PaymentSystem.repository.user.*;
import PaymentSystem.repository.payment.*;
import PaymentSystem.repository.paymentattempt.*;
import PaymentSystem.strategies.PaymentMethod;

public final class PaymentService {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentAttemptRepository attemptRepository;
    private final Map<PaymentMethodType, PaymentMethod> paymentMethods;

    public PaymentService(UserRepository userRepository, PaymentRepository paymentRepository, PaymentAttemptRepository attemptRepository, Map<PaymentMethodType, PaymentMethod> paymentMethods) {
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.attemptRepository = attemptRepository;
        this.paymentMethods = new EnumMap<>(PaymentMethodType.class);
        this.paymentMethods.putAll(paymentMethods);
    }

    public Optional<PaymentResult> processPayment(String idempotencyKey, String userId, BigDecimal amount, PaymentMethodType paymentMethodType) {

        PaymentMethod paymentMethod = paymentMethods.get(paymentMethodType);

        // Step 2: Prepare a candidate attempt.
        PaymentAttempt candidate = new PaymentAttempt(idempotencyKey, userId, amount, paymentMethodType, UUID.randomUUID().toString());

        // Step 3: Atomically claim this idempotency key.
        PaymentAttempt storedAttempt = attemptRepository.tryCreatingPaymentAttempt(candidate);  // no need to have synchronised .. since we are claiming in 1 step in concurrent hash map.

        boolean isNewAttempt = (storedAttempt == candidate);

        // Step 4: The key already existed.
        if (!isNewAttempt) {

            // Reject reuse of a key with different parameters.
            if (!storedAttempt.matchesRequest(userId, amount, paymentMethodType)) {
                return Optional.empty();
            }

            // Do not process the payment again.
            if (storedAttempt.getStatus() == PaymentAttemptStatus.IN_PROGRESS) {
                return Optional.of(PaymentResult.inProgress(storedAttempt.getPaymentId()));
            }

            // Return the original cached result.
            PaymentResult cachedResult = storedAttempt.getResult();

            if (cachedResult != null) {
                return Optional.of(cachedResult);
            }

            return Optional.of(PaymentResult.inProgress(storedAttempt.getPaymentId()));
        }

        // Step 5: Only the request that created the attempt
        // reaches the payment-processing logic.
        Payment payment = new Payment(storedAttempt.getPaymentId(), userId, amount, paymentMethodType);
        paymentRepository.save(payment);
        payment.markInProgress();

        PaymentExecutionResult executionResult;

        try {
            executionResult = paymentMethod.process(payment, idempotencyKey);
        } catch (RuntimeException exception) {
            return Optional.of(PaymentResult.inProgress(payment.getPaymentId()));
        }

        if (executionResult == null) {
            // Treat an absent provider result as indeterminate.
            return Optional.of(PaymentResult.inProgress(payment.getPaymentId()));
        }

        // Step 6: Persist the definitive payment outcome.
        if (executionResult.isSuccessful()) {
            payment.markSuccess();
            PaymentResult result = PaymentResult.success(payment.getPaymentId());
            storedAttempt.complete(result, PaymentAttemptStatus.COMPLETED);
            return Optional.of(result);
        } else {
            String reason = executionResult.getMessage();
            payment.markFailed(reason);
            PaymentResult result = PaymentResult.failed(payment.getPaymentId(), reason);
            storedAttempt.complete(result, PaymentAttemptStatus.FAILED);
            return Optional.of(result);
        }
    }
    
    // View a user's payment history.
    public List<Payment> getPaymentHistory(String userId) {
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User does not exist"));
        return paymentRepository.findByUserId(userId);
    }

    // View a specific payment, checking ownership.
    public Payment getPayment(String userId, String paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new IllegalArgumentException("Payment does not exist"));
        if (!payment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Payment does not belong to this user");
        }
        return payment;
    }
}