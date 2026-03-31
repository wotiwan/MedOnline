package com.wotiwan.medonline.database.repository;

import com.wotiwan.medonline.database.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {

    @Query("""
    select ts from TimeSlot ts
    join ts.schedule s
    where s.doctor.id = :doctorId
      and s.workDate = :date
      order by ts.startTime
""")
    List<TimeSlot> findAllByDoctorIdAndDate(Integer doctorId, LocalDate date);
}
