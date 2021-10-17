package com.notkamui.dto;

import io.micronaut.core.annotation.Introspected;

import java.util.Objects;

@Introspected
public record UserSaveDTO(String username, String password) {
    public UserSaveDTO {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
    }
}
