package com.wotiwan.medonline.dto;

import com.wotiwan.medonline.database.entity.AppointmentStatus;
import com.wotiwan.medonline.database.entity.Payment;
import com.wotiwan.medonline.database.entity.TimeSlot;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AppointmentReadDto(
        Integer id,
        UserReadDto user,
        DoctorReadDto doctor,
        TimeSlotReadDto timeSlot,
        AppointmentStatus status,
        String consultationResult,
        LocalDateTime createdAt,
        BigDecimal price,
        List<PaymentReadDto> payments
) {

}
