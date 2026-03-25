package com.wotiwan.medonline.dto;

import com.wotiwan.medonline.database.entity.Specialization;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class DoctorCreateDto {

    @NotNull(message = "User id is required")
    Integer userId;

    @NotNull(message = "Специализация обязательна")
    Integer specializationId;

    String description;
}