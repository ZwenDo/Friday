package com.kalia.friday.user;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Represents the body of a user response from the server to the client.
 */
@Introspected
public record UserResponseDTO(
    @NotNull UUID id,
    @NotNull @NotBlank String username
) {}
