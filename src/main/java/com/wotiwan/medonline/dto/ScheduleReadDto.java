package com.wotiwan.medonline.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleReadDto(
        Integer id,
        DoctorReadDto doctor,
        LocalDate workDate,
        LocalTime startTime,
        LocalTime endTime
) {
}
