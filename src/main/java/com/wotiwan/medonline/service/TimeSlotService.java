package com.wotiwan.medonline.service;

import com.wotiwan.medonline.database.entity.*;
import com.wotiwan.medonline.database.repository.AppointmentRepository;
import com.wotiwan.medonline.database.repository.TimeSlotRepository;
import com.wotiwan.medonline.database.repository.UserRepository;
import com.wotiwan.medonline.dto.TimeSlotReadDto;
import com.wotiwan.medonline.mapper.TimeSlotMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final TimeSlotMapper timeSlotMapper;
    private final DoctorService doctorService;

    public List<TimeSlotReadDto> findAllByDoctorAndDate(Integer doctorId, LocalDate date) {

        // Проверка, существует ли врач с таким id
        doctorService.checkDoctorExists(doctorId);

        // Проверка корректности запрашиваемой даты. Не отдаём то, что было в прошлом
        if (date == null) {
            date = LocalDate.now();
        }
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Дата не может быть в прошлом! date=%s".formatted(date));
        }

        return timeSlotRepository.findAllByDoctorIdAndDate(doctorId, date)
                .stream()
                .map(timeSlotMapper::map)
                .toList();
    }

    public void book(Integer slotId, String userEmail) {

        TimeSlot slot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new EntityNotFoundException("Слот с id=%d не найден!".formatted(slotId)));

        if (slot.isBooked()) {
            throw new IllegalStateException("Слот уже занят");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден!"));

        Doctor doctor = slot.getSchedule().getDoctor();

        slot.setBooked(true);

        Appointment appointment = Appointment.builder()
                .patient(user)
                .doctor(doctor)
                .timeSlot(slot)
                .status(AppointmentStatus.BOOKED)
                .price(doctor.getConsultationPrice())
                .build();

        appointmentRepository.save(appointment);
    }
}
