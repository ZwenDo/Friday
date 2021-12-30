package com.kalia.friday.user;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;

/**
 * Represents the body of a user credentials.
 */
@Introspected
public record UserCredsDTO(
    @NotBlank String username,
    @NotBlank String password
) {
}
