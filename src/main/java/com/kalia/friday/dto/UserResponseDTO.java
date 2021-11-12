package com.kalia.friday.dto;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotNull;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * Represents the body of a user response from the server to the client.
 */
@Introspected
public record UserResponseDTO(
    @NotNull UUID id,
    @NotNull String username
) {
    /**
     * Creates a body for the response
     *
     * @param id       the created uuid of the user
     * @param username the name of the user
     */
    public UserResponseDTO {
        requireNonNull(id);
        requireNonNull(username);
    }
}
