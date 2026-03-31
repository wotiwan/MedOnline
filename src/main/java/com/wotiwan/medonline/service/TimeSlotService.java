package com.wotiwan.medonline.service;

import com.wotiwan.medonline.database.entity.*;
import com.wotiwan.medonline.database.repository.AppointmentRepository;
import com.wotiwan.medonline.database.repository.TimeSlotRepository;
import com.wotiwan.medonline.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public List<TimeSlot> findAllByDoctorAndDate(Integer doctorId, LocalDate date) {
        return timeSlotRepository.findAllByDoctorIdAndDate(doctorId, date);
    }

    public void book(Integer slotId, String userEmail) {

        TimeSlot slot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found"));

        if (slot.isBooked()) {
            throw new IllegalStateException("Слот уже занят");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Doctor doctor = slot.getSchedule().getDoctor();

        slot.setBooked(true);

        Appointment appointment = Appointment.builder()
                .patient(user)
                .doctor(doctor)
                .timeSlot(slot)
                .status(AppointmentStatus.BOOKED)
                .build();

        appointmentRepository.save(appointment);
    }
}
