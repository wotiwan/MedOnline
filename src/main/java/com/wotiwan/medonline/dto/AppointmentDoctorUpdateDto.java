package com.wotiwan.medonline.dto;

import com.wotiwan.medonline.database.entity.AppointmentStatus;

public record AppointmentDoctorUpdateDto(
        AppointmentStatus status,
        String consultationResult
) {
}
