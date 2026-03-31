package com.wotiwan.medonline.database.repository;

import com.wotiwan.medonline.database.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    List<Appointment> findAllByPatientIdOrderByTimeSlot_StartTimeDesc(Integer patientId);

}
