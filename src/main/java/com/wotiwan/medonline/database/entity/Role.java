package com.wotiwan.medonline.database.entity;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;

public enum Role implements GrantedAuthority {
    ADMIN, PATIENT, DOCTOR;

    @Override
    public @Nullable String getAuthority() {
        return name();
    }

    public static boolean roleExists(String roleName) {
        return Arrays.stream(Role.values()).anyMatch(role -> roleName.equals(role.name()));
    }

}
