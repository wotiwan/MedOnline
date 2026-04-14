package com.wotiwan.medonline.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class RegisterRequest {

    @NotNull(message = "Имя должно быть указано")
    private String name;

    @NotNull(message = "Имя пользователя должно быть указано")
    private String username;

    @NotNull(message = "Пароль должен быть указан")
    @Length(message = "Длинна пароля должна быть от 5 символов", min = 5)
    private String password;

    @Email(message = "Введите корректную почту")
    private String email;
}