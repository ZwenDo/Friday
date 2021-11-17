package com.kalia.friday.user;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotEmpty;

/**
 * Represents the body of a user credentials.
 */
@Introspected
public record UserCredsDTO(
    @NotEmpty String username,
    @NotEmpty String password
) {
}
