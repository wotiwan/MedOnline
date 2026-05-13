package com.wotiwan.medonline.http.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wotiwan.medonline.dto.ResponseMessage;
import com.wotiwan.medonline.dto.payment.PaymentResponse;
import com.wotiwan.medonline.security.user.SecurityUser;
import com.wotiwan.medonline.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Создание оплаты
    @PostMapping("/{appointmentId}")
    public String createPayment(@PathVariable Integer appointmentId,
                                @RequestParam String returnUrl) {
        return paymentService.createPayment(appointmentId, returnUrl);
    }

    @PostMapping("/notification")
    public ResponseMessage<String> getNotificationOnPayment(
            @RequestBody String body
    ) throws JsonProcessingException {

        paymentService.updatePaymentStatus(body);

        return new ResponseMessage<>("OK!");
    }

    // Дадим через api возможность только админу делать возвраты.
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{paymentId}/refund")
    public ResponseMessage<?> makeRefund(@PathVariable Integer paymentId) {

        paymentService.refundPayment(paymentId);

        return new ResponseMessage<>("Средства успешно возвращены!");

    }

}
