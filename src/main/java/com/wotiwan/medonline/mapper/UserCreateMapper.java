package com.wotiwan.medonline.mapper;

import com.wotiwan.medonline.database.entity.User;
import com.wotiwan.medonline.dto.UserCreateDto;
import com.wotiwan.medonline.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCreateMapper implements Mapper<UserCreateDto, User>{

    private final PasswordEncoder passwordEncoder;

    @Override
    public User map(UserCreateDto object) {
        return User.builder()
                .email(object.email())
                .password(passwordEncoder.encode(object.password())) // Хеширование тут происходит. Мб переделать
                .firstName(object.firstName())
                .middleName(object.middleName())
                .lastName(object.lastName())
                .build();
    }
    @Override
    public User map(UserCreateDto fromObject, User toObject) {
        toObject.setEmail(fromObject.email());
        toObject.setPassword(passwordEncoder.encode(fromObject.password()));
        toObject.setFirstName(fromObject.firstName());
        toObject.setMiddleName(fromObject.middleName());
        toObject.setLastName(fromObject.lastName());
        return toObject;
    }
}
