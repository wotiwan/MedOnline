package com.wotiwan.medonline;

import com.wotiwan.medonline.database.entity.*;
import com.wotiwan.medonline.database.repository.AppointmentRepository;
import com.wotiwan.medonline.database.repository.TimeSlotRepository;
import com.wotiwan.medonline.database.repository.UserRepository;
import com.wotiwan.medonline.service.AppointmentService;
import com.wotiwan.medonline.service.TimeSlotService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class MedOnlineApplicationTests {

    @Test
    void contextLoads() {
    }

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @InjectMocks
    private TimeSlotService timeSlotService;

    @Test
    void shouldBookSlotSuccessfully() {

        Doctor doctor = new Doctor();
        doctor.setId(1);
        doctor.setConsultationPrice(BigDecimal.valueOf(1500));

        Schedule schedule = new Schedule();
        schedule.setDoctor(doctor);

        TimeSlot slot = TimeSlot.builder()
                .id(1)
                .isBooked(false)
                .schedule(schedule)
                .build();

        User user = new User();
        user.setId(10);
        user.setEmail("test@mail.com");

        when(timeSlotRepository.findById(1))
                .thenReturn(Optional.of(slot));

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> {
                    Appointment a = invocation.getArgument(0);
                    a.setId(100); // имитируем ID из БД
                    return a;
                });


        Integer appointmentId = timeSlotService.book(1, "test@mail.com");


        assertNotNull(appointmentId);
        assertEquals(100, appointmentId);
        assertTrue(slot.isBooked());


        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void shouldCancelAppointment() {

        User patient = new User();
        patient.setId(1);
        patient.setEmail("test@mail.com");

        Doctor doctor = new Doctor();
        doctor.setId(2);

        Schedule schedule = new Schedule();
        schedule.setDoctor(doctor);

        TimeSlot slot = new TimeSlot();
        slot.setId(10);
        slot.setBooked(true);
        slot.setSchedule(schedule);

        slot.setStartTime(LocalDateTime.now().plusHours(1));
        slot.setEndTime(LocalDateTime.now().plusHours(2));

        Appointment appointment = new Appointment();
        appointment.setId(100);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setTimeSlot(slot);
        appointment.setStatus(AppointmentStatus.BOOKED);

        appointment.setPayments(new ArrayList<>());

        when(appointmentRepository.findById(100))
                .thenReturn(Optional.of(appointment));

        appointmentService.cancel(100, "test@mail.com");

        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
        assertFalse(slot.isBooked());
    }

}
