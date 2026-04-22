package com.wotiwan.medonline.dto;

import com.wotiwan.medonline.database.entity.AppointmentStatus;
import com.wotiwan.medonline.database.entity.TimeSlot;

import java.time.LocalDateTime;

// TODO: Поидее могу же TimeSlot поменять отсюда? Тогда сделать дто
public record AppointmentReadDto(
        Integer id,
        UserReadDto user,
        DoctorReadDto doctor,
        TimeSlot timeSlot, // dto ?
        AppointmentStatus status,
        String consultationResult,
        LocalDateTime createdAt
) {

}
