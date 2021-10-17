package com.notkamui.dto;

import io.micronaut.core.annotation.Introspected;

import java.util.Objects;

@Introspected
public record UserDTO(Long id, String username, String password) {
    public UserDTO {
        Objects.requireNonNull(id);
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
    }
}
