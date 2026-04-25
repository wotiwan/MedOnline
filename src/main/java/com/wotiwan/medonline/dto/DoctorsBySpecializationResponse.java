package com.wotiwan.medonline.dto;

import java.util.List;

public record DoctorsBySpecializationResponse(
        List<DoctorReadDto> doctors,
        SpecializationReadDto specialization
) {
}
