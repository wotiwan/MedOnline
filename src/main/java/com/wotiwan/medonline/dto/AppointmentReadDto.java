package com.wotiwan.medonline.dto;

import com.wotiwan.medonline.database.entity.AppointmentStatus;
import com.wotiwan.medonline.database.entity.TimeSlot;

import java.time.LocalDateTime;

public record AppointmentReadDto(
        Integer id,
        UserReadDto user,
        DoctorReadDto doctor,
        TimeSlotReadDto timeSlot,
        AppointmentStatus status,
        String consultationResult,
        LocalDateTime createdAt
) {

}
