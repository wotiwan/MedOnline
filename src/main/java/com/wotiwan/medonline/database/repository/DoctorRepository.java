package com.wotiwan.medonline.database.repository;

import com.wotiwan.medonline.database.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    boolean existsByUserId(Integer userId);
}
