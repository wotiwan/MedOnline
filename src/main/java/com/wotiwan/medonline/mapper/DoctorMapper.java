package com.wotiwan.medonline.mapper;

import com.wotiwan.medonline.database.entity.Doctor;
import com.wotiwan.medonline.dto.DoctorReadDto;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapper implements Mapper<Doctor, DoctorReadDto>{

    @Override
    public DoctorReadDto map(Doctor object) {
        return new DoctorReadDto(
                object.getId(),
                object.getUser().getId(),
                object.getUser().getFirstName(),
                object.getUser().getMiddleName(),
                object.getUser().getLastName(),
                object.getSpecialization().getId(),
                object.getDescription()
        );
    }
}
