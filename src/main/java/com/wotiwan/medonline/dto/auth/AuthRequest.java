package com.wotiwan.medonline.dto.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthRequest {
    @NotNull(message = "Имя пользователя обязательно")
    private String username;

    @NotNull(message = "Пароль обязателен")
    private String password;
}
