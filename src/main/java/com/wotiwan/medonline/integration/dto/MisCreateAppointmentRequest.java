package com.wotiwan.medonline.integration.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MisCreateAppointmentRequest {
    public Integer doctorId;
    public Integer slotId;
    public Integer patientId;
}