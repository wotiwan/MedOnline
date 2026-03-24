package com.wotiwan.medonline.mapper;

import com.wotiwan.medonline.database.entity.User;
import com.wotiwan.medonline.dto.UserReadDto;
import org.springframework.stereotype.Component;

@Component
public class UserReadMapper implements Mapper<User, UserReadDto>{
    @Override
    public UserReadDto map(User object) {
        return new UserReadDto(
                object.getId(),
                object.getEmail(),
                object.getRole()
        );
    }
}
