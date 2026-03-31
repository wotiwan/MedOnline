package com.wotiwan.medonline.service;

import com.wotiwan.medonline.database.entity.Doctor;
import com.wotiwan.medonline.database.entity.Schedule;
import com.wotiwan.medonline.database.entity.ScheduleTemplate;
import com.wotiwan.medonline.database.entity.TimeSlot;
import com.wotiwan.medonline.database.repository.DoctorRepository;
import com.wotiwan.medonline.database.repository.ScheduleRepository;
import com.wotiwan.medonline.database.repository.ScheduleTemplateRepository;
import com.wotiwan.medonline.database.repository.TimeSlotRepository;
import com.wotiwan.medonline.dto.ScheduleTemplateCreateDto;
import com.wotiwan.medonline.dto.ScheduleTemplateReadDto;
import com.wotiwan.medonline.mapper.ScheduleTemplateCreateMapper;
import com.wotiwan.medonline.mapper.ScheduleTemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ScheduleTemplateRepository templateRepository;
    private final DoctorRepository doctorRepository;
    private final ScheduleTemplateRepository scheduleTemplateRepository;
    private final ScheduleTemplateCreateMapper scheduleTemplateCreateMapper;
    private final ScheduleTemplateMapper scheduleTemplateMapper;

    public List<ScheduleTemplateReadDto> findAllScheduleTemplatesByDoctorId(Integer doctorId) {
        return scheduleTemplateRepository.findAllByDoctorId(doctorId).stream()
                .map(scheduleTemplateMapper::map)
                .toList();
    }

    public void createScheduleTemplate(ScheduleTemplateCreateDto dto) {

        Doctor doctor = doctorRepository.findById(dto.doctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        if (scheduleTemplateRepository.existsByDoctorAndDayOfWeek(doctor, dto.dayOfWeek())) {
            throw new IllegalStateException("Шаблон для этого дня уже существует");
        }

        // Валидация (перенести мб?)
        if (dto.startTime().isAfter(dto.endTime())) {
            throw new IllegalArgumentException("Время начала позже времени окончания");
        }
        if (dto.slotDuration() <= 0) {
            throw new IllegalArgumentException("Некорректная длительность слота");
        }

        ScheduleTemplate template = scheduleTemplateCreateMapper.map(doctor, dto);
        scheduleTemplateRepository.save(template);
    }

    public void deleteTemplateById(Integer id) {
        scheduleTemplateRepository.deleteById(id);
    }

    // Лапша какая-то
    public void generateSchedules(Integer doctorId, int daysAhead) {
        List<ScheduleTemplate> templates = templateRepository.findAllByDoctorId(doctorId);

        for (int i = 0; i < daysAhead; i++) {
            LocalDate date = LocalDate.now().plusDays(i);
            DayOfWeek dayOfWeek = date.getDayOfWeek();

            templates.stream()
                    .filter(t -> t.getDayOfWeek() == dayOfWeek)
                    .forEach(template -> createScheduleWithSlots(template.getDoctor(), date, template));
        }
    }

    private void createScheduleWithSlots(Doctor doctor,
                                         LocalDate date,
                                         ScheduleTemplate template) {

        // защита от дублей
        if (scheduleRepository.existsByDoctorAndWorkDate(doctor, date)) {
            return;
        }

        Schedule schedule = Schedule.builder()
                .doctor(doctor)
                .workDate(date)
                .startTime(template.getStartTime())
                .endTime(template.getEndTime())
                .build();

        scheduleRepository.save(schedule);

        // Создание слотов для текущего расписания
        generateSlots(schedule, template.getSlotDuration());
    }

    private void generateSlots(Schedule schedule, Integer duration) {

        LocalDateTime start = schedule.getWorkDate().atTime(schedule.getStartTime());
        LocalDateTime end = schedule.getWorkDate().atTime(schedule.getEndTime());

        while (!start.plusMinutes(duration).isAfter(end)) {

            TimeSlot slot = TimeSlot.builder()
                    .schedule(schedule)
                    .startTime(start)
                    .endTime(start.plusMinutes(duration))
                    .isBooked(false)
                    .build();

            timeSlotRepository.save(slot);

            start = start.plusMinutes(duration);
        }
    }

    public void deleteAllByDoctor(Integer doctorId) {
        List<Schedule> schedules = scheduleRepository.findAllByDoctorId(doctorId);
        scheduleRepository.deleteAll(schedules);
    }

}
