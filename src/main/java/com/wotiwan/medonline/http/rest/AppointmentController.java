package com.wotiwan.medonline.http.rest;

import com.wotiwan.medonline.dto.AppointmentReadDto;
import com.wotiwan.medonline.security.user.SecurityUser;
import com.wotiwan.medonline.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/{id}")
    public AppointmentReadDto appointmentDetails(@PathVariable Integer id,
                                                 @AuthenticationPrincipal SecurityUser securityUser) {

        return appointmentService.findById(id, securityUser.getUser().getId());

    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, String>> cancelAppointment(
            @PathVariable Integer id,
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        appointmentService.cancel(id, securityUser.getUsername());
        return ResponseEntity.ok(
                Map.of("message", "Запись успешно отменена!")
        );
    }

}
