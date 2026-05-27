package com.wotiwan.medonline.integration.connector;

import com.wotiwan.medonline.dto.AppointmentReadDto;
import com.wotiwan.medonline.dto.DoctorReadDto;
import com.wotiwan.medonline.dto.TimeSlotReadDto;

import java.time.LocalDate;
import java.util.List;

public interface MisConnector {
    DoctorReadDto getDoctor(Long id);
    List<TimeSlotReadDto> getSlots(Long doctorId, LocalDate date);
    AppointmentReadDto book(Long slotId);
}