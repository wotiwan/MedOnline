package com.wotiwan.medonline.integration.controller;

import com.wotiwan.medonline.integration.dto.webhook.AppointmentCancelledWebhookDto;
import com.wotiwan.medonline.integration.dto.webhook.AppointmentCreatedWebhookDto;
import com.wotiwan.medonline.integration.dto.webhook.ScheduleUpdatedWebhookDto;
import com.wotiwan.medonline.integration.service.MisWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/integration/webhooks/mis")
@RequiredArgsConstructor
public class MisWebhookController {

    private final MisWebhookService webhookService;

    @PostMapping("/appointments/created")
    public ResponseEntity<Void> appointmentCreated(
            @RequestBody AppointmentCreatedWebhookDto dto
    ) {
        webhookService.handleAppointmentCreated(dto);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/appointments/cancelled")
    public ResponseEntity<Void> appointmentCancelled(
            @RequestBody AppointmentCancelledWebhookDto dto
    ) {
        webhookService.handleAppointmentCancelled(dto);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/schedule/updated")
    public ResponseEntity<Void> scheduleUpdated(
            @RequestBody ScheduleUpdatedWebhookDto dto
    ) {
        webhookService.handleScheduleUpdated(dto);

        return ResponseEntity.ok().build();
    }
}