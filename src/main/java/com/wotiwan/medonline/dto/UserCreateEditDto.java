package com.wotiwan.medonline.dto;

//TODO: validation
public record UserCreateEditDto(
        String email,
        String password
) {}
