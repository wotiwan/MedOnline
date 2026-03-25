package com.wotiwan.medonline.dto;

import com.wotiwan.medonline.database.entity.Role;

import java.time.LocalDate;

public record UserReadDto(
        Integer id,
        String email,
        String firstName,
        String middleName,
        String lastName,
        LocalDate birthDate,
        Role role
) {
}
