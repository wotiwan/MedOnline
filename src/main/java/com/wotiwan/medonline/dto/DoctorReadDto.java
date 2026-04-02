package com.wotiwan.medonline.dto;

public record DoctorReadDto (
    Integer id,
    Integer userId,
    String firstName,
    String middleName,
    String lastName,
    Integer specializationId,
    String description
){
}
