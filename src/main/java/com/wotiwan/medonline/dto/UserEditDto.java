package com.wotiwan.medonline.dto;

import java.time.LocalDate;

public record UserEditDto(
        String firstName,
        String middleName,
        String lastName,
        LocalDate birthDate
) {
}
