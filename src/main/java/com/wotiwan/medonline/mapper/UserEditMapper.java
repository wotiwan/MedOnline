package com.wotiwan.medonline.mapper;

import com.wotiwan.medonline.database.entity.User;
import com.wotiwan.medonline.dto.UserCreateDto;
import com.wotiwan.medonline.dto.UserEditDto;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserEditMapper implements Mapper<UserEditDto, User> {

    @Override
    public User map(UserEditDto object) {
        return User.builder()
                .firstName(object.firstName())
                .middleName(object.middleName())
                .lastName(object.lastName())
                .birthDate(object.birthDate())
                .build();
    }

    @Override
    public User map(UserEditDto fromObject, User toObject) {
        toObject.setFirstName(fromObject.firstName());
        toObject.setMiddleName(fromObject.middleName());
        toObject.setLastName(fromObject.lastName());
        toObject.setBirthDate(fromObject.birthDate());
        return toObject;
    }

}
