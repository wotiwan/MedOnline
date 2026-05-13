package com.wotiwan.medonline.http.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wotiwan.medonline.dto.ResponseMessage;
import com.wotiwan.medonline.dto.payment.PaymentResponse;
import com.wotiwan.medonline.service.PaymentService;
import lombok.RequiredArgsConstructor;
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

        paymentService.UpdatePaymentStatus(body);

        return new ResponseMessage<>("OK!");
    }

}
