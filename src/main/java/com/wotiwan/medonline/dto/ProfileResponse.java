package com.wotiwan.medonline.dto;

import java.util.List;

public record ProfileResponse(
    UserReadDto user,
    List<AppointmentReadDto> appointments
){}
