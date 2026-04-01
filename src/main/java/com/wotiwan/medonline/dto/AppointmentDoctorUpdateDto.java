package com.wotiwan.medonline.dto;

import com.wotiwan.medonline.database.entity.AppointmentStatus;

public record AppointmentDoctorUpdateDto(
        Integer id,
        AppointmentStatus status,
        String consultationResult
) {
}
