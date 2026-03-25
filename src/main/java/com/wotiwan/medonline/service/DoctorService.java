package com.wotiwan.medonline.service;

import com.wotiwan.medonline.database.entity.Doctor;
import com.wotiwan.medonline.database.entity.Role;
import com.wotiwan.medonline.database.entity.Specialization;
import com.wotiwan.medonline.database.entity.User;
import com.wotiwan.medonline.database.repository.DoctorRepository;
import com.wotiwan.medonline.database.repository.SpecializationRepository;
import com.wotiwan.medonline.database.repository.UserRepository;
import com.wotiwan.medonline.dto.DoctorCreateDto;
import com.wotiwan.medonline.dto.UserDoctorCreateDto;
import com.wotiwan.medonline.mapper.DoctorCreateMapper;
import com.wotiwan.medonline.mapper.UserCreateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final SpecializationRepository specializationRepository;
    private final DoctorCreateMapper doctorCreateMapper;
    private final UserCreateMapper userCreateMapper;

    // Создание доктора из юзера
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
        var specialization = specializationRepository.findById(dto.getSpecializationId())
                .orElseThrow(() -> new IllegalArgumentException("Specialization not found"));
        doctor.setSpecialization(specialization);
        doctorRepository.save(doctor);
    }
}