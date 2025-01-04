package com.querino.saga.payment.service;

import com.querino.saga.payment.domain.PaymentEvent;
import com.querino.saga.payment.domain.PaymentRepository;
import com.querino.saga.payment.domain.model.Payment;
import com.querino.saga.payment.domain.model.PaymentDTO;
import com.querino.saga.payment.domain.model.PaymentStatus;
import com.querino.saga.payment.kafka.PaymentEventProducer;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Optional<Payment> lastPayment = paymentRepository.findTopByOrderByIdDesc();
        if (lastPayment.isPresent()) {
            payment.setAmount(lastPayment.get().getAmount().add(paymentDTO.getAmount()));
        }else{
            payment.setAmount(paymentDTO.getAmount());
        }
        Payment savedPayment = paymentRepository.save(payment);

        kafkaProducer.sendOrder(
                new PaymentEvent(savedPayment.getId().toString(), PaymentStatus.PAYMENT_COMPLETED, savedPayment.toDTO())
        );
        return savedPayment.toDTO();
    }


    public PaymentDTO debit(@Valid PaymentDTO paymentDTO) {
        Payment payment = new Payment();
        payment.fromDTO(paymentDTO);
        Optional<Payment> lastPayment = paymentRepository.findTopByOrderByIdDesc();
        if (lastPayment.isPresent()) {
            payment.setAmount(lastPayment.get().getAmount().subtract(paymentDTO.getAmount()));
        }else{
            throw new InsuficientFundsException("Insufficient funds");
        }
        Payment savedPayment = paymentRepository.save(payment);

        kafkaProducer.sendOrder(
                new PaymentEvent(savedPayment.getId().toString(), PaymentStatus.PAYMENT_COMPLETED, savedPayment.toDTO())
        );
        return savedPayment.toDTO();
    }
}

