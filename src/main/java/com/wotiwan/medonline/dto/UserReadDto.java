package com.wotiwan.medonline.dto;

import com.wotiwan.medonline.database.entity.Role;

public record UserReadDto(
        Integer id,
        String email,
        Role role
) {
}
