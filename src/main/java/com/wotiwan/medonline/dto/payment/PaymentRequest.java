package com.wotiwan.medonline.dto.payment;

public record PaymentRequest(
        Amount amount,
        Confirmation confirmation,
        Boolean capture,
        String description
) {
    public record Amount(
            String value,
            String currency
    ) {}

    public record Confirmation(
            String type,
            String return_url
    ) {}
}
