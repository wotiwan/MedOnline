package com.wotiwan.medonline.database.repository;

import com.wotiwan.medonline.database.entity.Doctor;
import com.wotiwan.medonline.database.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    boolean existsByDoctorAndWorkDate(Doctor doctor, LocalDate date);

    List<Schedule> findAllByDoctorId(Integer doctorId);
}
