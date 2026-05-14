package com.wotiwan.medonline.dto;

import com.wotiwan.medonline.database.entity.Specialization;
import jakarta.validation.constraints.*;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class DoctorCreateDto {

    @NotNull(message = "User id is required")
    Integer userId;

    @NotNull(message = "Специализация обязательна")
    Integer specializationId;

    @PositiveOrZero
    BigDecimal consultationPrice;

    String description;
}