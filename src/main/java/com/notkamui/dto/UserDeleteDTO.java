package com.notkamui.dto;

import io.micronaut.core.annotation.Introspected;

import static java.util.Objects.requireNonNull;

@Introspected
public record UserDeleteDTO(String password) {
    public UserDeleteDTO {
        requireNonNull(password);
    }
}
