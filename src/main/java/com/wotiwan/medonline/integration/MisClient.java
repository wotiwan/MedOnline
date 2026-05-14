package com.wotiwan.medonline.integration;

import com.wotiwan.medonline.integration.dto.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class MisClient {

    private final WebClient webClient;

    public MisClient(WebClient misWebClient) {
        this.webClient = misWebClient;
    }

    // ВРАЧИ
    public List<MisDoctorDto> getDoctors() {
        return webClient.get()
                .uri("/mis/doctors")
                .retrieve()
                .bodyToFlux(MisDoctorDto.class)
                .collectList()
                .block();
    }

    // СЛОТЫ ВРАЧА
    public List<MisSlotDto> getDoctorSlots(Integer doctorId, String date) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/mis/doctors/{id}/slots")
                        .queryParam("date", date)
                        .build(doctorId))
                .retrieve()
                .bodyToFlux(MisSlotDto.class)
                .collectList()
                .block();
    }

    // СОЗДАНИЕ ЗАПИСИ
    public MisAppointmentDto createAppointment(MisCreateAppointmentRequest request) {
        return webClient.post()
                .uri("/mis/appointments")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(MisAppointmentDto.class)
                .block();
    }

    // ОТМЕНА ЗАПИСИ
    public void cancelAppointment(Integer appointmentId) {
        webClient.post()
                .uri("/mis/appointments/{id}/cancel", appointmentId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}