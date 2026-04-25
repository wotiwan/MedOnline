package com.wotiwan.medonline.dto;

import java.time.LocalDate;
import java.util.List;

public record DoctorsSlotsResponse(
        Integer doctorId,
        LocalDate date,
        List<TimeSlotReadDto> timeSlots
) {
}
