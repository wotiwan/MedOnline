package com.wotiwan.medonline.dto;

public record DoctorReadDto (
    Integer id,
    Integer userId,
    Integer specializationId,
    String description
){
}
