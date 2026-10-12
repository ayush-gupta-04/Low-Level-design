package PaymentSystem;

import PaymentSystem.repository.payment.InMemoryPaymentRepository;
import PaymentSystem.repository.payment.PaymentRepository;
import PaymentSystem.repository.paymentattempt.InMemoryPaymentAttemptRepository;
import PaymentSystem.repository.paymentattempt.PaymentAttemptRepository;
import PaymentSystem.repository.user.InMemoryUserRepository;
import PaymentSystem.repository.user.UserRepository;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

import PaymentSystem.entities.*;
import PaymentSystem.enums.*;
import PaymentSystem.strategies.*;
import PaymentSystem.strategies.pay.DebitCardPayment;
import PaymentSystem.strategies.pay.UPIPayment;
import PaymentSystem.service.*;

public class PaymentSystem {

    public static void main(String[] args) {

        UserRepository userRepository = new InMemoryUserRepository();
        PaymentRepository paymentRepository = new InMemoryPaymentRepository();
        PaymentAttemptRepository attemptRepository = new InMemoryPaymentAttemptRepository();

        userRepository.save(new User(
                "U101",
                "Ayush",
                "ayush@example.com"));

        // Register payment strategies.
        Map<PaymentMethodType, PaymentMethod> methods = new EnumMap<>(PaymentMethodType.class);

        methods.put(PaymentMethodType.DEBIT_CARD, new DebitCardPayment());
        methods.put(PaymentMethodType.UPI, new UPIPayment());

        PaymentService paymentService = new PaymentService(
                userRepository,
                paymentRepository,
                attemptRepository,
                methods);

        String idempotencyKey = "ORDER-1001-PAYMENT";
        BigDecimal amount = new BigDecimal("499.00");

        // First request.
        PaymentResult firstResult = paymentService.processPayment(
                        idempotencyKey,
                        "U101",
                        amount,
                        PaymentMethodType.UPI);

        System.out.println("First request:");
        System.out.println(firstResult);

        // Retry using the SAME key and request details.
        PaymentResult retryResult = paymentService.processPayment(
                        idempotencyKey,
                        "U101",
                        amount,
                        PaymentMethodType.UPI);

        System.out.println("\nRetry request:");
        System.out.println(retryResult);

        // Both results refer to the same payment.
        System.out.println("\nSame payment ID: " + firstResult.getPaymentId().equals(retryResult.getPaymentId()));

        // View payment history.
        System.out.println("\nPayment history:");

        for (Payment payment : paymentService.getPaymentHistory("U101")) {
            System.out.println(payment);
        }

        // Reusing the key with a different amount is rejected.
        try {
            paymentService.processPayment(
                    idempotencyKey,
                    "U101",
                    new BigDecimal("999.00"),
                    PaymentMethodType.UPI);

        } catch (IllegalArgumentException exception) {
            System.out.println("\nInvalid duplicate: " + exception.getMessage());
        }
    }
}