package com.wotiwan.medonline.service;

import com.wotiwan.medonline.database.entity.*;
import com.wotiwan.medonline.database.repository.AppointmentRepository;
import com.wotiwan.medonline.dto.AppointmentReadDto;
import com.wotiwan.medonline.mapper.AppointmentMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final PaymentService paymentService;

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

        // Запрещаем отменять запись всем, кроме самого пользователя
        if (!appointment.getPatient().getEmail().equals(email)) {
            throw new AccessDeniedException("Недостаточно прав доступа!");
        }

        // Запрещаем отменять запись после её начала
        if (appointment.getTimeSlot().getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Нельзя отменить, консультация уже началась!");
        }


        List<Payment> payments = appointment.getPayments();
        // Возвращаем платёж, если он был
        payments.stream()
                .filter(p -> p.getStatus().equals(PaymentStatus.SUCCEEDED))
                .forEach(p -> paymentService.refundPayment(p.getId()));
        // Отменяем незакрытые платежи. Не работает. Отменить PENDING платежи нельзя
        // TODO: обдумать как быть
//        payments.stream()
//                .filter(p -> p.getStatus().equals(PaymentStatus.PENDING))
//                .forEach(p -> paymentService.cancelPayment(p.getId()));

        appointment.setStatus(AppointmentStatus.CANCELLED);

        TimeSlot slot = appointment.getTimeSlot();

        // TODO: Повторно записаться на этот слот не получится, переделать
        slot.setBooked(false); // time_slot_id INT UNIQUE NOT NULL
    }
}
