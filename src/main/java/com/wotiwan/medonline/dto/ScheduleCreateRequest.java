package com.wotiwan.medonline.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Value;


public record ScheduleCreateRequest(
        @NotNull(message = "Количество дней для генерации расписания должно быть указано!")
        Integer daysAhead,

        @NotNull(message = "ID Врача должен быть указан!")
        Integer doctorId) {

}
