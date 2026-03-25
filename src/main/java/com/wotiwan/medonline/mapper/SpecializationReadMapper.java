package com.wotiwan.medonline.mapper;

import com.wotiwan.medonline.database.entity.Specialization;
import com.wotiwan.medonline.dto.SpecializationReadDto;
import org.springframework.stereotype.Component;

@Component
public class SpecializationReadMapper implements Mapper<Specialization, SpecializationReadDto>{

    @Override
    public SpecializationReadDto map(Specialization object) {
        return new SpecializationReadDto(
                object.getId(),
                object.getName()
        );
    }
}
