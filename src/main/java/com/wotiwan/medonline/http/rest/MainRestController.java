package com.wotiwan.medonline.http.rest;

import com.wotiwan.medonline.dto.SpecializationReadDto;
import com.wotiwan.medonline.service.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class MainRestController {

    private final SpecializationService specializationService;

    @GetMapping
    public List<SpecializationReadDto> mainPage() {
        return specializationService.findAll();
    }

}
