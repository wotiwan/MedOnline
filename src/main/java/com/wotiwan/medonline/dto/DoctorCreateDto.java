package com.wotiwan.medonline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class DoctorCreateDto {

    @NotNull(message = "User id is required")
    Integer userId;

    @NotBlank(message = "Специализация обязательна")
    String specialization;

    String description;
}