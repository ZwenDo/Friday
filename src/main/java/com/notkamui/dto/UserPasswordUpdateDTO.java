package com.notkamui.dto;

import io.micronaut.core.annotation.Introspected;

import static java.util.Objects.requireNonNull;

@Introspected
public record UserPasswordUpdateDTO(String oldPassword, String newPassword) {
    public UserPasswordUpdateDTO {
        requireNonNull(oldPassword);
        requireNonNull(newPassword);
    }
}
