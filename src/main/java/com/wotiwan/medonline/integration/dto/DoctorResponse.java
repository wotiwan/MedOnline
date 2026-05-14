package com.wotiwan.medonline.integration.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorResponse {

    private Integer id;

    private String firstName;
    private String lastName;
    private String middleName;

    private String specialization;
}