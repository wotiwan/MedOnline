package com.wotiwan.medonline.http.controller;

import com.wotiwan.medonline.database.entity.Appointment;
import com.wotiwan.medonline.database.entity.User;
import com.wotiwan.medonline.dto.UserCreateDto;
import com.wotiwan.medonline.dto.UserEditDto;
import com.wotiwan.medonline.dto.UserReadDto;
import com.wotiwan.medonline.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/profile")
@Controller
public class UserController {

    private final UserService userService;

    @GetMapping
    public String profilePage(Model model, Principal principal) {

        UserReadDto user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("user", user);

        List<Appointment> appointments =
                userService.findAllUserAppointmentsByUserEmail(principal.getName());

        model.addAttribute("appointments", appointments);

        return "user/profile";
    }

    @PostMapping
    public String edit(Model model,
                       @ModelAttribute @Validated UserEditDto userEdit,
                       Principal principal) {

        String username = principal.getName();

        UserReadDto userReadDto = userService.findByEmail(username).get(); // TODO: Переделать бы по-человечески

        return userService.update(userReadDto.id(), userEdit)
                .map(user -> {
                    model.addAttribute("user", user);
                    return "redirect:/profile";
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST)); // Какое исключение кинуть?
    }

}
