package com.querino.saga.payment.service;

import com.querino.saga.payment.domain.PaymentRepository;
import com.querino.saga.payment.domain.model.Payment;
import com.querino.saga.payment.domain.model.PaymentDTO;
import com.querino.saga.payment.domain.model.PaymentStatus;
import com.querino.saga.payment.domain.model.exception.InsuficientFundsException;
import com.querino.saga.payment.domain.model.inventory.InventoryEvent;
import com.querino.saga.payment.kafka.PaymentEventProducer;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer kafkaProducer;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, PaymentEventProducer kafkaProducer) {
        this.paymentRepository = paymentRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public PaymentDTO credit(@Valid PaymentDTO paymentDTO) {
        Payment payment = new Payment();
        payment.fromDTO(paymentDTO);
        Payment lastPayment = paymentRepository.findTopByCustomerIdOrderByIdDesc(payment.getCustomerId());
        if (lastPayment != null) {
            payment.setAmount(lastPayment.getAmount().add(paymentDTO.getAmount()));
        } else {
            payment = new Payment(null, PaymentStatus.PAYMENT_COMPLETED, paymentDTO.getAmount(), paymentDTO.getCustomerId());
        }
        Payment savedPayment = paymentRepository.save(payment);

        return savedPayment.toDTO();
    }


    public PaymentDTO debit(InventoryEvent inventoryEvent) {

        Optional<Payment> first = paymentRepository.findByCustomerId(inventoryEvent.getCustomerId()).stream().findFirst();
        Payment savedPayment;
        try {
            Payment payment = first.orElseThrow(() -> new InsuficientFundsException("Customer does not have balance"));
            BigDecimal totalAmount = getTotalAmount(inventoryEvent);

            validateSufficientBalance(totalAmount, payment);

            payment.setAmount(payment.getAmount().subtract(totalAmount));
            savedPayment = paymentRepository.save(payment);

            return savedPayment.toDTO();
        } catch (InsuficientFundsException e) {
            kafkaProducer.sendError(
                    new InventoryEvent(inventoryEvent.getCustomerId(), inventoryEvent.getInventoryStatus(), inventoryEvent.getProductDTO(), inventoryEvent.getTransactionId())
            );
            throw e;
        }
    }

    private static void validateSufficientBalance(BigDecimal amountDTO, Payment payment) {
        if (payment.getAmount().compareTo(amountDTO) < 0) {
            throw new InsuficientFundsException("Insufficient funds for customerId " + payment.getCustomerId());
        }
    }

    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream().map(Payment::toDTO).toList();
    }


    private static BigDecimal getTotalAmount(InventoryEvent inventoryEvent) {

        double amount = inventoryEvent.getProductDTO().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        return BigDecimal.valueOf(amount);
    }
}

