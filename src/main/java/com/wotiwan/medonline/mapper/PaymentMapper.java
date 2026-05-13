package com.wotiwan.medonline.mapper;

import com.wotiwan.medonline.database.entity.Payment;
import com.wotiwan.medonline.dto.PaymentReadDto;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper implements Mapper<Payment, PaymentReadDto> {

    @Override
    public PaymentReadDto map(Payment object) {
        return new PaymentReadDto(
                object.getId(),
                object.getExternalPaymentId(),
                object.getAmount(),
                object.getStatus(),
                object.getCreatedAt()
        );
    }
}
