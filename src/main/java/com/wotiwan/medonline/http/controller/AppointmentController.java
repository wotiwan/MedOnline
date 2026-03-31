package com.wotiwan.medonline.http.controller;

import com.wotiwan.medonline.database.entity.Appointment;
import com.wotiwan.medonline.service.AppointmentService;
import com.wotiwan.medonline.service.DoctorService;
import com.wotiwan.medonline.service.SpecializationService;
import com.wotiwan.medonline.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;

@Controller
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final DoctorService doctorService;
    private final TimeSlotService timeSlotService;
    private final AppointmentService appointmentService;

    @GetMapping("/{id}")
    public String appointmentDetails(@PathVariable Integer id,
                                     Model model,
                                     Principal principal) {


        Appointment appointment = appointmentService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        // защита (чтобы нельзя было смотреть чужие записи)
        if (!appointment.getPatient().getEmail().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        model.addAttribute("appointment", appointment);

        return "appointments/details";
    }

    @PostMapping("/cancel")
    public String cancel(@RequestParam Integer appointmentId,
                         Principal principal,
                         RedirectAttributes redirectAttributes) {

        appointmentService.cancel(appointmentId, principal.getName());

        redirectAttributes.addFlashAttribute("success", "Запись отменена");

        return "redirect:/profile";
    }

    @GetMapping("/create")
    public String create(@RequestParam Integer doctorId,
                         @RequestParam(required = false)
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                         LocalDate date,
                         Model model) {

        if (date == null || date.isBefore(LocalDate.now())) {
            date = LocalDate.now();
        }

        model.addAttribute("doctor", doctorService.findById(doctorId).orElseThrow());
        model.addAttribute("slots", timeSlotService.findAllByDoctorAndDate(doctorId, date));
        model.addAttribute("date", date);
        model.addAttribute("doctorId", doctorId);

        return "appointments/create";
    }

    @PostMapping("/book")
    public String book(@RequestParam Integer slotId,
                       @RequestParam Integer doctorId,
                       @RequestParam String date,
                       Principal principal,
                       RedirectAttributes redirectAttributes) {

        try {
            timeSlotService.book(slotId, principal.getName());
            redirectAttributes.addFlashAttribute("success", "Вы успешно записаны!");
        } catch (ObjectOptimisticLockingFailureException e) {
            redirectAttributes.addFlashAttribute("error", "Слот только что заняли");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/appointments/create?doctorId="  + doctorId + "&date=" + date;
    }
}