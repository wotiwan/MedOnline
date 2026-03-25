package com.wotiwan.medonline.dto;

//TODO: validation
public record UserCreateDto(
        String email,
        String password,
        String firstName,
        String middleName,
        String lastName
) {}
