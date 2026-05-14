package com.wotiwan.medonline.integration.service;


import com.wotiwan.medonline.database.entity.TimeSlot;
import com.wotiwan.medonline.integration.MisClient;
import com.wotiwan.medonline.integration.dto.*;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MisService {

    private final MisClient misClient;

    public MisService(MisClient misClient) {
        this.misClient = misClient;
    }

    // ВРАЧИ
    public List<DoctorResponse> getDoctors() {
        return misClient.getDoctors()
                .stream()
                .map(this::mapDoctor)
                .toList();
    }

    private DoctorResponse mapDoctor(MisDoctorDto dto) {

        DoctorResponse response = new DoctorResponse();

        response.setId(dto.getId());
        response.setFirstName(dto.getFirstName());
        response.setLastName(dto.getLastName());
        response.setMiddleName(dto.getMiddleName());
        response.setSpecialization(dto.getSpecialization());

        return response;
    }

    // СЛОТЫ
    public List<TimeSlot> getSlots(Integer doctorId, String date) {
        return misClient.getDoctorSlots(doctorId, date)
                .stream()
                .map(this::mapSlot)
                .toList();
    }

    private TimeSlot mapSlot(MisSlotDto dto) {
        TimeSlot slot = new TimeSlot();
        slot.setId(dto.id);
        slot.setStartTime(LocalDateTime.parse(dto.startTime));
        slot.setEndTime(LocalDateTime.parse(dto.endTime));
        slot.setBooked(dto.available);
        return slot;
    }

    // СОЗДАНИЕ ЗАПИСИ
    public MisAppointmentDto createAppointment(
            Integer doctorId,
            Integer slotId,
            Integer patientId
    ) {

        MisCreateAppointmentRequest request =
                new MisCreateAppointmentRequest();

        request.setDoctorId(doctorId);
        request.setSlotId(slotId);
        request.setPatientId(patientId);

        return misClient.createAppointment(request);
    }

    // ОТМЕНА
    public void cancelAppointment(Integer appointmentId) {
        misClient.cancelAppointment(appointmentId);
    }
}