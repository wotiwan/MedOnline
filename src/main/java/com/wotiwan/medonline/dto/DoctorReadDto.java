package com.wotiwan.medonline.dto;

import java.math.BigDecimal;

public record DoctorReadDto (
    Integer id,
    Integer userId,
    String firstName,
    String middleName,
    String lastName,
    Integer specializationId,
    BigDecimal consultationPrice,
    String description
){
}
