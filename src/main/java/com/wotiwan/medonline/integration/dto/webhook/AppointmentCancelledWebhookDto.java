package com.wotiwan.medonline.integration.dto.webhook;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentCancelledWebhookDto {

    private Integer slotId;
    private Integer appointmentId;

}