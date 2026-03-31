package com.wotiwan.medonline.http.controller;

import com.wotiwan.medonline.service.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final SpecializationService specializationService;

    @GetMapping("/")
    public String mainPage() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("specializations", specializationService.findAll());
        return "main/home";
    }

}
