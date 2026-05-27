package com.wotiwan.medonline.integration.connector;

import com.wotiwan.medonline.dto.AppointmentReadDto;
import com.wotiwan.medonline.dto.DoctorReadDto;
import com.wotiwan.medonline.dto.TimeSlotReadDto;
import com.wotiwan.medonline.integration.connector.MisConnector;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MedeskMisConnector implements MisConnector {

    private final RestTemplate restTemplate;

    @Value("${integration.mis.medesk.base-url}")
    private String baseUrl;

    @Value("${integration.mis.medesk.api-key}")
    private String apiKey;

    @Override
    public DoctorReadDto getDoctor(Long id) {
        String url = baseUrl + "/doctors/" + id;

        HttpEntity<Void> request = new HttpEntity<>(authHeaders());

        ResponseEntity<DoctorReadDto> response =
                restTemplate.exchange(url, HttpMethod.GET, request, DoctorReadDto.class);

        return response.getBody();
    }

    @Override
    public List<TimeSlotReadDto> getSlots(Long doctorId, LocalDate date) {
        String url = baseUrl + "/doctors/" + doctorId + "/slots?date=" + date;

        HttpEntity<Void> request = new HttpEntity<>(authHeaders());

        ResponseEntity<TimeSlotReadDto[]> response =
                restTemplate.exchange(url, HttpMethod.GET, request, TimeSlotReadDto[].class);

        return Arrays.asList(response.getBody());
    }

    @Override
    public AppointmentReadDto book(Long slotId) {
        String url = baseUrl + "/appointments";

        HttpEntity<?> request = new HttpEntity<>(
                new BookRequest(slotId),
                authHeaders()
        );

        ResponseEntity<AppointmentReadDto> response =
                restTemplate.exchange(url, HttpMethod.POST, request, AppointmentReadDto.class);

        return response.getBody();
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        return headers;
    }

    private record BookRequest(Long slotId) {}
}