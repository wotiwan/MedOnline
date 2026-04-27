package com.wotiwan.medonline.http.rest;

import com.wotiwan.medonline.dto.SpecializationReadDto;
import com.wotiwan.medonline.service.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/specializations")
@RequiredArgsConstructor
public class SpecializationController {

    private final SpecializationService specializationService;

    @GetMapping("/{id}")
    public SpecializationReadDto getSpecialization(@PathVariable Integer id) {
        return specializationService.findById(id);
    }

    @GetMapping
    public List<SpecializationReadDto> getAllSpecializations() {
        return specializationService.findAll();
    }

}
