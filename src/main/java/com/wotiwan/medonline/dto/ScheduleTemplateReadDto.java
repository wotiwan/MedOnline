package com.wotiwan.medonline.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record ScheduleTemplateReadDto(
        Integer id,
        Integer doctorId,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        Integer slotDuration
) {
}
