package com.kalia.friday.user;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Represents the body of a user credentials.
 */
@Introspected
public record UserCredsDTO(
    @NotNull @NotBlank String username,
    @NotNull @NotBlank String password
) {}
