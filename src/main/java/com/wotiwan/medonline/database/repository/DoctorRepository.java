package com.wotiwan.medonline.database.repository;

import com.wotiwan.medonline.database.entity.Doctor;
import com.wotiwan.medonline.database.entity.Specialization;
import com.wotiwan.medonline.dto.DoctorReadDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    boolean existsByUserId(Integer userId);

    List<Doctor> findAllBySpecializationId(Integer specializationId);
}
