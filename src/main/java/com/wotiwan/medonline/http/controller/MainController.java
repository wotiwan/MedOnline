package com.wotiwan.medonline.http.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String mainPage() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home() {
        return "main/home";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')") // доступ только для админа
    public String admin() {
        return "main/admin";
    }

}
