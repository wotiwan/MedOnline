package com.wotiwan.medonline.database.entity;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN, PATIENT, DOCTOR;

    @Override
    public @Nullable String getAuthority() {
        return name();
    }
}
