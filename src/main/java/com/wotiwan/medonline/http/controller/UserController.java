package com.wotiwan.medonline.http.controller;

import com.wotiwan.medonline.dto.UserReadDto;
import com.wotiwan.medonline.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequiredArgsConstructor
@RequestMapping("/profile")
@Controller
public class UserController {

    private final UserService userService;

    @GetMapping
    public String profilePage(Model model, Principal principal) {
        // Получаем имя текущего залогиненного пользователя
        String username = principal.getName();

        // Находим его по юзернейму и кладём в модель
        return userService.findByEmail(username)
                .map(user -> {
                    model.addAttribute("user", user);
                    return "user/profile";
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
