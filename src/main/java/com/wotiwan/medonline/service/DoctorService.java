package com.wotiwan.medonline.service;

import com.wotiwan.medonline.database.entity.*;
import com.wotiwan.medonline.database.repository.AppointmentRepository;
import com.wotiwan.medonline.database.repository.DoctorRepository;
import com.wotiwan.medonline.database.repository.SpecializationRepository;
import com.wotiwan.medonline.database.repository.UserRepository;
import com.wotiwan.medonline.dto.AppointmentDoctorUpdateDto;
import com.wotiwan.medonline.dto.DoctorCreateDto;
import com.wotiwan.medonline.dto.DoctorReadDto;
import com.wotiwan.medonline.dto.UserDoctorCreateDto;
import com.wotiwan.medonline.mapper.DoctorCreateMapper;
import com.wotiwan.medonline.mapper.DoctorMapper;
import com.wotiwan.medonline.mapper.UserCreateMapper;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final SpecializationRepository specializationRepository;
    private final DoctorCreateMapper doctorCreateMapper;
    private final UserCreateMapper userCreateMapper;
    private final DoctorMapper doctorMapper;
    private final AppointmentRepository appointmentRepository;

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

    public Optional<DoctorReadDto> findById(Integer id) {
        return doctorRepository.findById(id)
                .map(doctorMapper::map);
    }

    public Optional<DoctorReadDto> findByUserId(Integer id) {
        return doctorRepository.findByUserId(id)
                .map(doctorMapper::map);
    }

    public List<DoctorReadDto> findAllBySpecialization(Integer specializationId) {
        return doctorRepository.findAllBySpecializationId(specializationId).stream()
                .map(doctorMapper::map)
                .toList();
    }

    public List<Appointment> getTodayAppointments(Integer doctorId) {
        return appointmentRepository.findTodayAppointments(doctorId);
    }

    public void updateAppointment(AppointmentDoctorUpdateDto dto) {
        Appointment appointment = appointmentRepository.findById(dto.id())
                .orElseThrow(() -> new IllegalArgumentException("Запись не найдена"));

        appointment.setStatus(dto.status());
        appointment.setConsultationResult(dto.consultationResult());
    }

}