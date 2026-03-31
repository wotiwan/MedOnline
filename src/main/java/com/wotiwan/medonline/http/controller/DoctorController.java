package com.wotiwan.medonline.http.controller;

import com.wotiwan.medonline.service.DoctorService;
import com.wotiwan.medonline.service.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;
    private final SpecializationService specializationService;

    @GetMapping
    public String doctors(@RequestParam Integer specializationId, Model model) {
        model.addAttribute("doctors",
                doctorService.findAllBySpecialization(specializationId));

        model.addAttribute("specialization",
                specializationService.findById(specializationId)
                        .orElseThrow(() -> new IllegalArgumentException("Doctor not found")));

        return "main/doctors";
    }

}
