package com.wotiwan.medonline.dto.payment;

public record PaymentResponse(
        String id,
        String status,
        Confirmation confirmation
) {
    public record Confirmation(
            String confirmation_url
    ) {}
}
