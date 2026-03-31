package com.wotiwan.medonline.service;

import com.wotiwan.medonline.database.entity.Appointment;
import com.wotiwan.medonline.database.entity.AppointmentStatus;
import com.wotiwan.medonline.database.entity.TimeSlot;
import com.wotiwan.medonline.database.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    // TODO: Возвращать DTO
    public Optional<Appointment> findById(Integer id) {
        return appointmentRepository.findById(id);
    }

    @Transactional
    public void cancel(Integer id, String email) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow();

        if (!appointment.getPatient().getEmail().equals(email)) {
            throw new RuntimeException("Нет доступа");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);

        TimeSlot slot = appointment.getTimeSlot();

        // TODO: Повторно записаться на этот слот не получится, переделать
        slot.setBooked(false); // time_slot_id INT UNIQUE NOT NULL
    }

}
