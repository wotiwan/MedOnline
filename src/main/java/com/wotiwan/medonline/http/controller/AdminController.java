package com.wotiwan.medonline.http.controller;

import com.wotiwan.medonline.database.entity.Doctor;
import com.wotiwan.medonline.database.entity.Role;
import com.wotiwan.medonline.database.entity.ScheduleTemplate;
import com.wotiwan.medonline.database.entity.Specialization;
import com.wotiwan.medonline.dto.*;
import com.wotiwan.medonline.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final DoctorService doctorService;
    private final UserDoctorService userDoctorService;
    private final SpecializationService specializationService;
    private final ScheduleService scheduleService;

    @GetMapping
    public String admin() {
        return "admin/admin";
    }

    // список пользователей
    @GetMapping("/users")
    public String users(Model model, @RequestParam(required = false) String role) {

        // Фильтрация по роли
        if (role == null || role.isBlank() || role.equals("ALL")) {
            model.addAttribute("users", userService.findAll());
        } else if (role.equals("ADMIN")) {
            model.addAttribute("users", userService.findAllByRole(Role.ADMIN));
        } else if (role.equals("DOCTOR")) {
            model.addAttribute("users", userService.findAllByRole(Role.DOCTOR));
        } else if (role.equals("PATIENT")) {
            model.addAttribute("users", userService.findAllByRole(Role.PATIENT));
        }

        model.addAttribute("rolesFilter", new String[]{"ALL", "ADMIN", "DOCTOR", "PATIENT"});
        // Предустанавливаем значение в зависимости от полученного значения параметра
        model.addAttribute("selectedRole", role == null ? "ALL" : role);
        return "admin/users";
    }

    // смена роли
    // TODO: добавить проверку успешности изменения роли
    @PostMapping("/users/{id}/role")
    public String updateRole(@PathVariable Integer id,
                             @RequestParam Role role) {

        userService.updateRole(id, role);
        return "redirect:/admin/users";
    }

    // удаление
    @PostMapping("/users/{id}/delete")
    public String delete(RedirectAttributes redirectAttributes, @PathVariable Integer id, Principal principal) {
        UserReadDto currentUser = userService.findByEmail(principal.getName()).orElse(null);

        if (Objects.equals(id, currentUser.id())) {
            redirectAttributes.addFlashAttribute("errors", "Can't delete yourself!".lines().toList());
            return "redirect:/admin/users";
        }
        userService.delete(id);
        return "redirect:/admin/users";
    }

    // Ссылка на форму создания профиля врача
    // TODO: поменять пути эндпоинтов, чтобы логичнее выглядело
    @GetMapping("/doctors/create")
    public String createForm(@RequestParam Integer userId, Model model) {

        var user = userService.findById(userId)
                .orElseThrow();

        model.addAttribute("user", user);
        model.addAttribute("specializations", specializationService.findAll());
        model.addAttribute("doctor", new DoctorCreateDto(userId, null, ""));

        return "admin/doctor-create";
    }

    // Создание профиля врача
    @PostMapping("/doctors/create")
    public String create(@ModelAttribute @Validated DoctorCreateDto dto,
                         RedirectAttributes redirectAttributes) {

        try {
            doctorService.create(dto);
            redirectAttributes.addFlashAttribute("success", "Врач успешно создан");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/doctors/create?userId=" + dto.getUserId();
        }

        return "redirect:/admin/users";
    }

    // Создание Юзера + Врача за раз
    @GetMapping("/doctors/full/create")
    public String createForm(Model model) {
        model.addAttribute("specializations", specializationService.findAll());
        model.addAttribute("doctor", new UserDoctorCreateDto("", "", "", "", "", null, null, ""));
        return "admin/doctor-full-create";
    }

    @PostMapping("/doctors/full/create")
    public String create(@ModelAttribute("doctor") @Valid UserDoctorCreateDto dto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/doctor-full-create";
        }

        try {
            userDoctorService.create(dto);
            redirectAttributes.addFlashAttribute("success", "Врач создан успешно");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/doctors/full/create";
        }

        return "redirect:/admin/users";
    }


    // Управление расписанием врачей

    // Страница с созданием

    // Страница с настройкой расписания конкретного врача
    // TODO: doctor_id и user_id - не одно и то же, из-за этого будет путаница. Стоит как-то поменять
    @GetMapping("/schedules/doctor/{userId}")
    public String doctorSchedulePage(@PathVariable Integer userId, Model model) {

        DoctorReadDto doctor = doctorService.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        // Шаблоны расписания для врача
        List<ScheduleTemplateReadDto> templates = scheduleService.findAllScheduleTemplatesByDoctorId(doctor.id());

        model.addAttribute("doctor", doctor);
        model.addAttribute("templates", templates);
        model.addAttribute("newTemplate", new ScheduleTemplateCreateDto(doctor.id(), null, null, null, null));
        model.addAttribute("hasTemplates", !templates.isEmpty());
        model.addAttribute("daysOfWeek", DayOfWeek.values());

        return "admin/doctor-schedule"; // Thymeleaf view
    }

    @PostMapping("/schedules/doctor/{doctorId}/template/create")
    public String createTemplate(@PathVariable Integer doctorId,
                                 @ModelAttribute @Validated ScheduleTemplateCreateDto newTemplate,
                                 RedirectAttributes redirectAttributes) {
        DoctorReadDto doctor = doctorService.findById(doctorId).orElseThrow();
        try {
            scheduleService.createScheduleTemplate(newTemplate);
            redirectAttributes.addFlashAttribute("success", "Шаблон создан");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/schedules/doctor/" + doctor.userId();
    }

    @PostMapping("/schedules/doctor/{doctorId}/template/{templateId}/delete")
    public String deleteTemplate(@PathVariable Integer doctorId,
                                 @PathVariable Integer templateId,
                                 RedirectAttributes redirectAttributes) {
        DoctorReadDto doctor = doctorService.findById(doctorId).orElseThrow();
        try {
            scheduleService.deleteTemplateById(templateId);
            redirectAttributes.addFlashAttribute("success", "Шаблон удалён");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/schedules/doctor/" + doctor.userId();
    }

    // Генерация расписания на n дней вперёд
    @PostMapping("/schedules/doctor/{doctorId}/generate")
    public String generateSchedule(@PathVariable Integer doctorId,
                                   @RequestParam(defaultValue = "7") int daysAhead,
                                   RedirectAttributes redirectAttributes) {
        DoctorReadDto doctor = doctorService.findById(doctorId).orElseThrow();
        try {
            scheduleService.generateSchedules(doctorId, daysAhead);
            redirectAttributes.addFlashAttribute("success", "Расписание сгенерировано");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/schedules/doctor/" + doctor.userId();
    }

    // Удаление расписания (всё) у врача
    @PostMapping("/schedules/doctor/{doctorId}/delete")
    public String deleteSchedule(@PathVariable Integer doctorId,
                                 RedirectAttributes redirectAttributes) {
        DoctorReadDto doctor = doctorService.findById(doctorId).orElseThrow();
        try {
            scheduleService.deleteAllByDoctor(doctorId);
            redirectAttributes.addFlashAttribute("success", "Расписание удалено");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/schedules/doctor/" + doctor.userId();
    }

}