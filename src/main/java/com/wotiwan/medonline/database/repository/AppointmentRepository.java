package com.wotiwan.medonline.database.repository;

import com.wotiwan.medonline.database.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    List<Appointment> findAllByPatientIdOrderByTimeSlot_StartTimeDesc(Integer patientId);

    @Query("""
        select a from Appointment a
        where a.doctor.id = :doctorId
        and current_date = a.timeSlot.schedule.workDate
    """)
    List<Appointment> findTodayAppointments(Integer doctorId);

}
