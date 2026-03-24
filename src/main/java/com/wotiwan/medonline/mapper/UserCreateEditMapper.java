package com.wotiwan.medonline.mapper;

import com.wotiwan.medonline.database.entity.User;
import com.wotiwan.medonline.dto.UserCreateEditDto;
import com.wotiwan.medonline.util.PasswordUtil;
import org.springframework.stereotype.Component;

@Component
public class UserCreateEditMapper implements Mapper<UserCreateEditDto, User>{

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
        toObject.setPassword(PasswordUtil.hashPassword(fromObject.password()));
        return toObject;
    }
}
