package com.querino.saga.payment.domain;

import com.querino.saga.payment.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByCustomerId(Long customerId);
    Payment findTopByCustomerIdOrderByIdDesc(Long customerId);
}