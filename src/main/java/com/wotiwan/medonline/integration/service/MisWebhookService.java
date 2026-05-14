package com.wotiwan.medonline.integration.service;

import com.wotiwan.medonline.database.entity.Appointment;
import com.wotiwan.medonline.database.entity.AppointmentStatus;
import com.wotiwan.medonline.database.entity.TimeSlot;
import com.wotiwan.medonline.database.repository.AppointmentRepository;
import com.wotiwan.medonline.database.repository.TimeSlotRepository;
import com.wotiwan.medonline.integration.dto.webhook.AppointmentCancelledWebhookDto;
import com.wotiwan.medonline.integration.dto.webhook.AppointmentCreatedWebhookDto;
import com.wotiwan.medonline.integration.dto.webhook.ScheduleUpdatedWebhookDto;
import com.wotiwan.medonline.service.AppointmentService;
import com.wotiwan.medonline.service.TimeSlotService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MisWebhookService {

    private final AppointmentRepository appointmentRepository;
    private final TimeSlotRepository timeSlotRepository;

    // Создание записи в МИС
    public void handleAppointmentCreated(AppointmentCreatedWebhookDto dto) {

        log.info("MIS webhook: appointment created, slotId={}", dto.getSlotId());

        TimeSlot slot = timeSlotRepository.findById(dto.getSlotId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Slot not found: " + dto.getSlotId()
                ));

        // 1. блокируем слот
        slot.setBooked(true);

        // 2. ищем локальную запись (она уже создана через book())
        Appointment appointment = appointmentRepository.findByTimeSlotId(slot.getId())
                .orElse(null);

        if (appointment != null) {
            appointment.setStatus(AppointmentStatus.BOOKED);
        }
    }

    // Отмена записи в МИС - освобождаем слот
    public void handleAppointmentCancelled(AppointmentCancelledWebhookDto dto) {

        log.info("MIS webhook: appointment cancelled, slotId={}", dto.getSlotId());

        TimeSlot slot = timeSlotRepository.findById(dto.getSlotId())
                .orElse(null);

        if (slot == null) return;

        slot.setBooked(false);

        Appointment appointment = appointmentRepository.findByTimeSlotId(slot.getId())
                .orElse(null);

        if (appointment != null) {
            appointment.setStatus(AppointmentStatus.CANCELLED);
        }
    }

    // Обновление расписания врача
    public void handleScheduleUpdated(ScheduleUpdatedWebhookDto dto) {

        log.info("MIS webhook: schedule updated doctorId={}", dto.getDoctorId());

        // просто пересоздаём слоты
        // (у тебя это уже есть логически в TimeSlotService)

        // ВАЖНО: здесь можно просто удалить будущие слоты врача
        // и дать системе пересоздать их
    }
}