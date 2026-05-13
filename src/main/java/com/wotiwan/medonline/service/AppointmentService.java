package com.wotiwan.medonline.service;

import com.wotiwan.medonline.database.entity.Appointment;
import com.wotiwan.medonline.database.entity.AppointmentStatus;
import com.wotiwan.medonline.database.entity.TimeSlot;
import com.wotiwan.medonline.database.repository.AppointmentRepository;
import com.wotiwan.medonline.dto.AppointmentReadDto;
import com.wotiwan.medonline.mapper.AppointmentMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    public AppointmentReadDto findById(Integer id, Integer userId) {
        // Проверяем, что эта запись принадлежит текущему пользователю (запрет просмотра чужих записей)
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Запись c id=%d не найдена!".formatted(id)));

        // Доступ к записи есть у врача или пациента
        if (appointment.getPatient().getId().equals(userId)
                || appointment.getDoctor().getId().equals(userId)) {
            return appointmentMapper.map(appointment);
        } else {
            throw new AccessDeniedException("Недостаточно прав доступа!");
        }
    }

    public AppointmentReadDto findById(Integer id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Запись c id=%d не найдена!".formatted(id)));
        return appointmentMapper.map(appointment);
    }

    @Transactional
    public void cancel(Integer id, String email) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Запись c id=%d не найдена!".formatted(id)));

        if (!appointment.getPatient().getEmail().equals(email)) {
            throw new AccessDeniedException("Недостаточно прав доступа!");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);

        TimeSlot slot = appointment.getTimeSlot();

        // TODO: Повторно записаться на этот слот не получится, переделать
        slot.setBooked(false); // time_slot_id INT UNIQUE NOT NULL
    }
}
