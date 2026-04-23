package com.wotiwan.medonline.dto;

import java.time.LocalDateTime;

public record TimeSlotReadDto(
        Integer id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        boolean isBooked
) {
}
