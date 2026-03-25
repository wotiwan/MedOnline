package com.wotiwan.medonline.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDate;

@Value
public class UserDoctorCreateDto {

    @NotBlank(message = "Email обязателен")
    @Email(message = "Неверный формат email")
    String email;

    @NotBlank(message = "Пароль обязателен")
    String password;

    @NotBlank(message = "Имя обязательно")
    String firstName;

    String middleName;

    @NotBlank(message = "Фамилия обязательна")
    String lastName;

    @NotNull(message = "Дата рождения обязательна")
    LocalDate birthDate;

    @NotBlank(message = "Специализация обязательна")
    String specialization;

    String description;
}