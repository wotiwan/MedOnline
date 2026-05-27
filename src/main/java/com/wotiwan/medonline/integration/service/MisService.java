package com.wotiwan.medonline.integration.service;


import com.wotiwan.medonline.database.entity.TimeSlot;
import com.wotiwan.medonline.dto.AppointmentReadDto;
import com.wotiwan.medonline.dto.DoctorReadDto;
import com.wotiwan.medonline.dto.TimeSlotReadDto;
import com.wotiwan.medonline.integration.connector.MisConnector;
import com.wotiwan.medonline.integration.dto.*;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MisService {

    private final MisConnector misConnector;

    public MisService(@Qualifier("oneCMisConnector") MisConnector misConnector) { // Можем подменить реализацию
        this.misConnector = misConnector;
    }

    public DoctorReadDto getDoctor(Long id) {
        return misConnector.getDoctor(id);
    }

    public List<TimeSlotReadDto> getSlots(Long doctorId, LocalDate date) {
        return misConnector.getSlots(doctorId, date);
    }

    public AppointmentReadDto book(Long slotId) {
        return misConnector.book(slotId);
    }
}