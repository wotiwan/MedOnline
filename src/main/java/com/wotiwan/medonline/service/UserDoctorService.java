package com.wotiwan.medonline.service;

import com.wotiwan.medonline.database.entity.Doctor;
import com.wotiwan.medonline.database.entity.Role;
import com.wotiwan.medonline.database.entity.User;
import com.wotiwan.medonline.database.repository.DoctorRepository;
import com.wotiwan.medonline.database.repository.SpecializationRepository;
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
    private final SpecializationRepository specializationRepository;
    private final UserCreateMapper userCreateMapper;
    private final DoctorCreateMapper doctorCreateMapper;

    // Создание доктора и юзера одновременно
    public void create(UserDoctorCreateDto dto) {
        // Создание юзера
        User user = userCreateMapper.map(dto);
        user.setRole(Role.DOCTOR);
        userRepository.save(user);
        // Создание доктора
        var specialization = specializationRepository.findById(dto.getSpecializationId())
                        .orElseThrow(() -> new IllegalArgumentException("Specialization not found"));
        Doctor doctor = doctorCreateMapper.map(dto);
        doctor.setSpecialization(specialization);
        doctorRepository.save(doctor);
    }
}
