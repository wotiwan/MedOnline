package com.wotiwan.medonline.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wotiwan.medonline.database.entity.Appointment;
import com.wotiwan.medonline.database.entity.Payment;
import com.wotiwan.medonline.database.entity.PaymentStatus;
import com.wotiwan.medonline.database.repository.AppointmentRepository;
import com.wotiwan.medonline.database.repository.PaymentRepository;
import com.wotiwan.medonline.dto.AppointmentReadDto;
import com.wotiwan.medonline.dto.payment.PaymentRequest;
import com.wotiwan.medonline.dto.payment.PaymentResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final WebClient webClient;
    private final AppointmentService appointmentService;
    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;

    @Value("${yookassa.shop-id}")
    private String shopId;

    @Value("${yookassa.secret-key}")
    private String secretKey;

    @Transactional
    public String createPayment(Integer appointmentId) {

        // Проверяем, есть ли уже существующие попытки оплаты
        List<Payment> payments = paymentRepository.findAllByAppointmentId(appointmentId);

        // Если запись уже оплачена - запрещаем повторную оплату
        boolean alreadyPaid = payments.stream()
                .anyMatch(e -> e.getStatus().equals(PaymentStatus.SUCCEEDED));
        if (alreadyPaid) {
            throw new IllegalStateException("Appointment is already paid!");
        }

        // Проверяем, есть ли незакрытые попытки оплаты
        Payment payment = payments.stream()
                .filter(e -> e.getStatus().equals(PaymentStatus.PENDING))
                .findAny().orElse(null);
        // Если незакрытые платежи есть - отправляем пользователя на старую страницу оплаты
        if (payment != null) {
            PaymentResponse response = webClient.get()
                    .uri("https://api.yookassa.ru/v3/payments/{id}", payment.getExternalPaymentId())
                    .headers(headers -> {
                        headers.setBasicAuth(shopId, secretKey);
                    })
                    .retrieve()
                    .bodyToMono(PaymentResponse.class)
                    .block();
            return response.confirmation().confirmation_url();
        }

        // Находим стоимость консультации по её id
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Appointment with id=%d not found!".formatted(appointmentId)));

        BigDecimal paymentPrice = appointment.getPrice();

        var request = new PaymentRequest(
                new PaymentRequest.Amount(
                        paymentPrice.toString(),
                        "RUB"
                ),
                new PaymentRequest.Confirmation(
                        "redirect",
                        "http://localhost:8080/main"
                ),
                true,
                "Оплата консультации"
        );

        PaymentResponse response = webClient.post()
                .uri("https://api.yookassa.ru/v3/payments")
                .headers(headers -> {
                    headers.setBasicAuth(shopId, secretKey);
                    headers.set("Idempotence-Key", UUID.randomUUID().toString());
                })
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentResponse.class)
                .block();

        System.out.println(response);

        // Создаём сущность платежа
        paymentRepository.save(Payment.builder()
                .externalPaymentId(response.id())
                .amount(paymentPrice)
                .status(PaymentStatus.PENDING)
                .appointment(appointment)
                .build());

        return response.confirmation().confirmation_url();

    }

    @Transactional
    public void UpdatePaymentStatus(String requestBody) throws JsonProcessingException {

        // Парсим json ответ от yookassa
        JsonNode json = objectMapper.readTree(requestBody);
        String paymentId = json
                .get("object")
                .get("id")
                .asText();
        String status = json
                .get("object")
                .get("status")
                .asText();

        Payment payment = paymentRepository.findByExternalPaymentId(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment with id=%s not found!".formatted(paymentId)));

        payment.setStatus(PaymentStatus.valueOf(status.toUpperCase()));
    }

}