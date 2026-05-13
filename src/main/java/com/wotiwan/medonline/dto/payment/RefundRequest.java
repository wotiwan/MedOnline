package com.wotiwan.medonline.dto.payment;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RefundRequest(
        Amount amount,

        @JsonProperty("payment_id")
        String paymentId
) {

    public record Amount(
            BigDecimal value,
            String currency
    ) {}
}