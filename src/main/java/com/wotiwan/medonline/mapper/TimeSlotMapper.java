package com.wotiwan.medonline.mapper;

import com.wotiwan.medonline.database.entity.TimeSlot;
import com.wotiwan.medonline.dto.TimeSlotReadDto;
import org.springframework.stereotype.Component;

@Component
public class TimeSlotMapper implements Mapper<TimeSlot, TimeSlotReadDto> {
    @Override
    public TimeSlotReadDto map(TimeSlot object) {
        return new TimeSlotReadDto(
                object.getId(),
                object.getStartTime(),
                object.getEndTime(),
                object.isBooked()
        );
    }
}
