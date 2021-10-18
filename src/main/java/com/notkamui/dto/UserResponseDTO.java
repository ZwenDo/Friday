package com.notkamui.dto;

import io.micronaut.core.annotation.Introspected;

import java.util.Objects;

@Introspected
public record UserResponseDTO(Long id, String username) {
    public UserResponseDTO {
        Objects.requireNonNull(id);
        Objects.requireNonNull(username);
    }
}
