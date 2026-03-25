package com.wotiwan.medonline.mapper;

import com.wotiwan.medonline.database.entity.User;
import com.wotiwan.medonline.dto.UserCreateEditDto;
import com.wotiwan.medonline.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCreateEditMapper implements Mapper<UserCreateEditDto, User>{

    private final PasswordEncoder passwordEncoder;

    @Override
    public User map(UserCreateEditDto object) {
        return User.builder()
                .email(object.email())
                .password(PasswordUtil.hashPassword(object.password())) // Хеширование тут происходит. Мб переделать
                .build();
    }
    @Override
    public User map(UserCreateEditDto fromObject, User toObject) {
        toObject.setEmail(fromObject.email());
        toObject.setPassword(passwordEncoder.encode(fromObject.password()));
        return toObject;
    }
}
