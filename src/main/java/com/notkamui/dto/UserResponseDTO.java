package com.notkamui.dto;

import io.micronaut.core.annotation.Introspected;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents the body of a user response from the server to the client.
 */
@Introspected
public record UserResponseDTO(UUID id, String username) {
    /**
     * Creates a body for the response
     *
     * @param id       the created uuid of the user
     * @param username the name of the user
     */
    public UserResponseDTO {
        Objects.requireNonNull(id);
        Objects.requireNonNull(username);
    }
}
