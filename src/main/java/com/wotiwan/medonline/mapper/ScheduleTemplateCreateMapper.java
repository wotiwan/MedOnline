package com.wotiwan.medonline.mapper;

import com.wotiwan.medonline.database.entity.Doctor;
import com.wotiwan.medonline.database.entity.ScheduleTemplate;
import com.wotiwan.medonline.dto.ScheduleTemplateCreateDto;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ScheduleTemplateCreateMapper implements Mapper<ScheduleTemplateCreateDto, ScheduleTemplate> {

    @Override
    public ScheduleTemplate map(ScheduleTemplateCreateDto object) {
        ScheduleTemplate scheduleTemplate = new ScheduleTemplate();
        scheduleTemplate.setDayOfWeek(object.dayOfWeek());
        scheduleTemplate.setStartTime(object.startTime());
        scheduleTemplate.setEndTime(object.endTime());
        scheduleTemplate.setSlotDuration(object.slotDuration());
        return scheduleTemplate;
    }

    public ScheduleTemplate map(Doctor doctor, ScheduleTemplateCreateDto object) {
        ScheduleTemplate scheduleTemplate = new ScheduleTemplate();
        scheduleTemplate.setDoctor(doctor);
        scheduleTemplate.setDayOfWeek(object.dayOfWeek());
        scheduleTemplate.setStartTime(object.startTime());
        scheduleTemplate.setEndTime(object.endTime());
        scheduleTemplate.setSlotDuration(object.slotDuration());
        return scheduleTemplate;
    }
}
