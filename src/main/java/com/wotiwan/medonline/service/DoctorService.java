package com.wotiwan.medonline.service;

import com.wotiwan.medonline.database.entity.Doctor;
import com.wotiwan.medonline.database.entity.Role;
import com.wotiwan.medonline.database.entity.User;
import com.wotiwan.medonline.database.repository.DoctorRepository;
import com.wotiwan.medonline.database.repository.UserRepository;
import com.wotiwan.medonline.dto.DoctorCreateDto;
import com.wotiwan.medonline.mapper.DoctorCreateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final DoctorCreateMapper doctorCreateMapper;

    public void create(DoctorCreateDto dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Защита от повторного создания врача
        if (doctorRepository.existsByUserId(user.getId())) {
            throw new IllegalStateException("Doctor already exists for this user");
        }

        // Меняем роль
        user.setRole(Role.DOCTOR);

        // Создаём Doctor
        Doctor doctor = doctorCreateMapper.map(dto);

        doctorRepository.save(doctor);
    }
}