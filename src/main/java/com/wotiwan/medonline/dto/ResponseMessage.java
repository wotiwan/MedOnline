package com.wotiwan.medonline.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

// TODO: Разделить сообщение об ошибке и сообщения со статус-кодом 200 на два класса

@Data
@AllArgsConstructor
@JsonPropertyOrder({ "message", "data", "errorTime" }) // Для корректного порядка записи при сериализации в json
public class ResponseMessage<T>
{
    private String message; // Человеко читаемое сообщение об ошибке
    private T data; // Подробная информация
    private LocalDateTime errorTime;

    public ResponseMessage(String message) {
        this.message = message;
        this.errorTime = LocalDateTime.now();
    }
    public ResponseMessage(String message, T data) {
        this.message = message;
        this.data = data;
        this.errorTime = LocalDateTime.now();
    }

}
