package com.notkamui.dto;

import io.micronaut.core.annotation.Introspected;

import java.util.Objects;
import java.util.UUID;

@Introspected
public record UserResponseDTO(UUID id, String username) {
    public UserResponseDTO {
        Objects.requireNonNull(id);
        Objects.requireNonNull(username);
    }
}
