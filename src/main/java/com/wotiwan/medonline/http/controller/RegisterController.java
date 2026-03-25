package com.wotiwan.medonline.http.controller;

import com.wotiwan.medonline.dto.UserCreateDto;
import com.wotiwan.medonline.dto.UserReadDto;
import com.wotiwan.medonline.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
@RequestMapping("/register")
public class RegisterController {

    private final UserService userService;

    @GetMapping
    public String register() {
        return "user/register";
    }

    // Валидация автоматом посредством аннотации @Validated (в Dto поля помечены валидационными аннотациями)
    @PostMapping
    public String create(@ModelAttribute @Validated UserCreateDto user,
                         BindingResult bindingResult, // Сохраняет в себя ошибки валидации
                         RedirectAttributes redirectAttributes) {
        // Здесь валидация
        if (bindingResult.hasErrors()) {
            // Если ошибка валидации - редиректим обратно на форму регистрации, но поля уже предзаполнены
            redirectAttributes.addFlashAttribute("user", user);
            // Отдаём ошибки на фронт
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/user/register";
        }
        UserReadDto userReadDto = userService.create(user); // Пока ничего не делаем?
        return "redirect:/user/login";
    }

}
