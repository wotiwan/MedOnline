package com.wotiwan.medonline.http.controller;

import com.wotiwan.medonline.database.entity.Role;
import com.wotiwan.medonline.dto.DoctorCreateDto;
import com.wotiwan.medonline.dto.UserDoctorCreateDto;
import com.wotiwan.medonline.dto.UserReadDto;
import com.wotiwan.medonline.service.DoctorService;
import com.wotiwan.medonline.service.UserDoctorService;
import com.wotiwan.medonline.service.UserService;
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
import java.util.Objects;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final DoctorService doctorService;
    private final UserDoctorService userDoctorService;

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
        model.addAttribute("doctor", new DoctorCreateDto(userId, "", ""));

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
        model.addAttribute("doctor", new UserDoctorCreateDto("", "", "", "", "", null, "", ""));
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

}