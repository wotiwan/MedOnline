package com.wotiwan.medonline.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RefundResponse(

        String id,

        String status,

        Amount amount,

        @JsonProperty("payment_id")
        String paymentId,

        @JsonProperty("created_at")
        LocalDateTime createdAt

) {

    public record Amount(
            BigDecimal value,
            String currency
    ) {}
}