package com.wotiwan.medonline.service;

import com.wotiwan.medonline.database.entity.Appointment;
import com.wotiwan.medonline.database.entity.AppointmentStatus;
import com.wotiwan.medonline.database.entity.TimeSlot;
import com.wotiwan.medonline.database.repository.AppointmentRepository;
import com.wotiwan.medonline.dto.AppointmentReadDto;
import com.wotiwan.medonline.mapper.AppointmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    //TODO: Переделать exception'ы, сервис не должен знать про http. Добавить кастомный
    public AppointmentReadDto findById(Integer id, Integer userId) {
        // Проверяем, что эта запись принадлежит текущему пользователю (запрет просмотра чужих записей)
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (appointment.getPatient().getId().equals(userId)) {
            return appointmentMapper.map(appointment);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
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
