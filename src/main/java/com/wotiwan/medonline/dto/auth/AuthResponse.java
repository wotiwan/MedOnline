package com.wotiwan.medonline.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String message;
    private String token;

    public AuthResponse(String message) {
        this.message = message;
    }
}