package com.wotiwan.medonline.database.repository;

import com.wotiwan.medonline.database.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
}
