package com.wotiwan.medonline.dto;

import com.wotiwan.medonline.database.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentReadDto(
        Integer id,
        String externalPaymentId,
        BigDecimal amount,
        PaymentStatus status,
        LocalDateTime createdAt
) {

}
