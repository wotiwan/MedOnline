package com.wotiwan.medonline.service;

import com.wotiwan.medonline.database.entity.Role;
import com.wotiwan.medonline.database.entity.User;
import com.wotiwan.medonline.database.repository.DoctorRepository;
import com.wotiwan.medonline.database.repository.UserRepository;
import com.wotiwan.medonline.dto.UserDoctorCreateDto;
import com.wotiwan.medonline.mapper.DoctorCreateMapper;
import com.wotiwan.medonline.mapper.UserCreateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDoctorService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final UserCreateMapper userCreateMapper;
    private final DoctorCreateMapper doctorCreateMapper;

    // Создание доктора и юзера одновременно
    public void create(UserDoctorCreateDto dto) {
        User user = userCreateMapper.map(dto);
        user.setRole(Role.DOCTOR);
        userRepository.save(user);
        doctorRepository.save(doctorCreateMapper.map(dto));
    }
}
