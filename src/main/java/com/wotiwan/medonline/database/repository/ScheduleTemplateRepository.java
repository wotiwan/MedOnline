package com.wotiwan.medonline.database.repository;

import com.wotiwan.medonline.database.entity.Doctor;
import com.wotiwan.medonline.database.entity.ScheduleTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface ScheduleTemplateRepository extends JpaRepository<ScheduleTemplate, Integer> {

    List<ScheduleTemplate> findAllByDoctor(Doctor doctor);

    boolean existsByDoctorAndDayOfWeek(Doctor doctor, DayOfWeek dayOfWeek);

    List<ScheduleTemplate> findAllByDoctorId(Integer doctorId);
}
