package com.wotiwan.medonline.mapper;

import com.wotiwan.medonline.database.entity.Appointment;
import com.wotiwan.medonline.database.entity.Doctor;
import com.wotiwan.medonline.dto.AppointmentReadDto;
import com.wotiwan.medonline.dto.DoctorReadDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentMapper implements Mapper<Appointment, AppointmentReadDto>{

    private final UserReadMapper userReadMapper;
    private final DoctorMapper doctorMapper;

    @Override
    public AppointmentReadDto map(Appointment object) {
        return new AppointmentReadDto(
                object.getId(),
                userReadMapper.map(object.getPatient()),
                doctorMapper.map(object.getDoctor()),
                object.getTimeSlot(),
                object.getStatus(),
                object.getConsultationResult(),
                object.getCreatedAt()
        );
    }
}
