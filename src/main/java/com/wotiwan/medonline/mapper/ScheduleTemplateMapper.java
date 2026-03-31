package com.wotiwan.medonline.mapper;

import com.wotiwan.medonline.database.entity.ScheduleTemplate;
import com.wotiwan.medonline.dto.ScheduleTemplateReadDto;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTemplateMapper implements Mapper<ScheduleTemplate, ScheduleTemplateReadDto> {

    @Override
    public ScheduleTemplateReadDto map(ScheduleTemplate object) {
        return new ScheduleTemplateReadDto(
                object.getId(),
                object.getDoctor().getId(),
                object.getDayOfWeek(),
                object.getStartTime(),
                object.getEndTime(),
                object.getSlotDuration()
        );
    }
}
