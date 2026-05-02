package com.wotiwan.medonline.http.rest;

import com.wotiwan.medonline.dto.ResponseMessage;
import com.wotiwan.medonline.dto.ScheduleCreateRequest;
import com.wotiwan.medonline.dto.ScheduleTemplateCreateDto;
import com.wotiwan.medonline.dto.ScheduleTemplateReadDto;
import com.wotiwan.medonline.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/doctor/create")
    public ResponseEntity<ResponseMessage<?>> generateSchedule(
            @Validated @RequestBody ScheduleCreateRequest scheduleCreateRequest) {

        scheduleService.generateSchedules(scheduleCreateRequest.doctorId(), scheduleCreateRequest.daysAhead());

        return ResponseEntity.ok().body(new ResponseMessage<>("Расписание создано!"));
    }

    @GetMapping("/doctor/templates")
    public List<ScheduleTemplateReadDto> getDoctorScheduleTemplates(
            @RequestParam Integer doctorId) {
        return scheduleService.findAllScheduleTemplatesByDoctorId(doctorId);
    }

    @PostMapping("/template/create")
    public ResponseEntity<ResponseMessage<?>> createTemplate(
            @Validated @RequestBody ScheduleTemplateCreateDto scheduleTemplate
            ) {

        scheduleService.createScheduleTemplate(scheduleTemplate);

        return ResponseEntity.ok().body(new ResponseMessage<>("Шаблон создан!"));
    }

    @DeleteMapping("/template/{id}")
    public ResponseEntity<ResponseMessage<?>> deleteTemplate(@PathVariable Integer id) {

        scheduleService.deleteTemplateById(id);

        return ResponseEntity.ok().body(new ResponseMessage<>("Шаблон удалён!"));

    }

}
