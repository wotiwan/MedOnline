package com.wotiwan.medonline.integration.dto.webhook;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentCreatedWebhookDto {

    private Integer appointmentId;

    private Integer slotId;

    private Integer doctorId;

    private Integer patientId;

    private String startTime;

    private String endTime;
}