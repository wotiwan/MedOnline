package com.wotiwan.medonline.http.rest;

import com.wotiwan.medonline.dto.*;
import com.wotiwan.medonline.security.user.SecurityUser;
import com.wotiwan.medonline.service.AppointmentService;
import com.wotiwan.medonline.service.DoctorService;
import com.wotiwan.medonline.service.SpecializationService;
import com.wotiwan.medonline.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DoctorRestController {

    private final DoctorService doctorService;
    private final SpecializationService specializationService;
    private final TimeSlotService timeSlotService;
    private final AppointmentService appointmentService;

    @GetMapping("/specialization/{specializationId}/doctors")
    public DoctorsBySpecializationResponse doctors(@PathVariable Integer specializationId) {

        List<DoctorReadDto> doctors = doctorService.findAllBySpecialization(specializationId);
        SpecializationReadDto specialization = specializationService.findById(specializationId);

        return new DoctorsBySpecializationResponse(doctors, specialization);

    }

    // Возвращает инфу об этом докторе
    @GetMapping("/doctors/{doctorId}")
    public DoctorReadDto getDoctor(@PathVariable Integer doctorId) {
        return doctorService.findById(doctorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Врач не найден!"));
    }

    // Возвращает информацию о доступных слотах записи запрашиваемого врача
    @GetMapping("/doctors/{doctorId}/slots")
    public DoctorsSlotsResponse getDoctorSlots(@PathVariable Integer doctorId,
                                             @RequestParam(required = false)
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // Проверка, существует ли врач с таким id
        if (!doctorService.existsById(doctorId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // Проверка корректности запрашиваемой даты. Не отдаём то, что было в прошлом
        if (date == null) {
            date = LocalDate.now();
        }
        if (date.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата не может быть в прошлом");
        }

        List<TimeSlotReadDto> timeSlots = timeSlotService.findAllByDoctorAndDate(doctorId, date);

        return new DoctorsSlotsResponse(doctorId, date, timeSlots);
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/doctor/appointments")
    public List<AppointmentReadDto> getDoctorTodayAppointments(
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        return doctorService.getTodayAppointments(securityUser.getUser().getId());
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/doctor/appointments/{appointmentId}")
    public AppointmentReadDto getDoctorAppointment(
            @PathVariable Integer appointmentId,
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        return appointmentService.findById(appointmentId, securityUser.getUser().getId());
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PutMapping("/doctor/appointments/{appointmentId}")
    public ResponseEntity<Map<String, String>> updateDoctorAppointment(
            @PathVariable Integer appointmentId,
            @RequestBody AppointmentDoctorUpdateDto appointment,
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        doctorService.updateAppointment(appointmentId, appointment);
        return ResponseEntity.ok().body(Map.of("message", "Запись обновлена!"));
    }

}
