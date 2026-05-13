package com.wotiwan.medonline.database.repository;

import com.wotiwan.medonline.database.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Optional<Payment> findByExternalPaymentId(String externalPaymentId);

    List<Payment> findAllByAppointmentId(Integer appointmentId);
}
