package com.wotiwan.medonline.http.controller;

import com.wotiwan.medonline.database.entity.Doctor;
import com.wotiwan.medonline.dto.AppointmentDoctorUpdateDto;
import com.wotiwan.medonline.dto.DoctorReadDto;
import com.wotiwan.medonline.dto.UserReadDto;
import com.wotiwan.medonline.service.DoctorService;
import com.wotiwan.medonline.service.SpecializationService;
import com.wotiwan.medonline.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final UserService userService;
    private final SpecializationService specializationService;

    @GetMapping("/doctors")
    public String doctors(@RequestParam Integer specializationId, Model model) {
        model.addAttribute("doctors",
                doctorService.findAllBySpecialization(specializationId));

        model.addAttribute("specialization",
                specializationService.findById(specializationId)
                        .orElseThrow(() -> new IllegalArgumentException("Doctor not found")));

        return "main/doctors";
    }

    // TODO: Не пускать на страницу с префиксом /doctor никого кроме врачей
    @GetMapping("/doctor/appointments")
    public String doctorAppointments(Model model, Principal principal) {

        UserReadDto user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        DoctorReadDto doctor = doctorService.findByUserId(user.id())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        model.addAttribute("appointments",
                doctorService.getTodayAppointments(doctor.id()));

        return "doctor/appointments";
    }

    // TODO: Запретить врачу смотреть чужие записи
    @GetMapping("/doctor/appointments/{id}")
    public String appointmentDetails(@PathVariable Integer id, Model model) {
        model.addAttribute("appointmentId", id);
        model.addAttribute("updateDto", new AppointmentDoctorUpdateDto(null, null, null));
        return "doctor/appointment-details";
    }

    // TODO: Запретить врачу менять чужие записи
    @PostMapping("doctor/appointments/{id}")
    public String update(@PathVariable Integer id,
                         @ModelAttribute AppointmentDoctorUpdateDto dto) {

        doctorService.updateAppointment(dto);
        return "redirect:/doctor/appointments";
    }

}
