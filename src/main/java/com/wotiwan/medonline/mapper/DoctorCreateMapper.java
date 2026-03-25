package com.wotiwan.medonline.mapper;

import com.wotiwan.medonline.database.entity.Doctor;
import com.wotiwan.medonline.database.entity.User;
import com.wotiwan.medonline.database.repository.UserRepository;
import com.wotiwan.medonline.dto.DoctorCreateDto;
import com.wotiwan.medonline.dto.UserDoctorCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DoctorCreateMapper implements Mapper<DoctorCreateDto, Doctor> {

    private final UserRepository userRepository;

    @Override
    public Doctor map(DoctorCreateDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return Doctor.builder()
                .user(user)
                .description(dto.getDescription())
                .build();
    }

    // Вариация для создания доктора и юзера одновременно
    public Doctor map(UserDoctorCreateDto dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return Doctor.builder()
                .user(user)
                .description(dto.getDescription())
                .build();
    }
}