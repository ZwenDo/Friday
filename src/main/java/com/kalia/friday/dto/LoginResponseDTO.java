package com.kalia.friday.dto;

import io.micronaut.core.annotation.Introspected;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * Represents the body of a login response from the server to the client.
 */
@Introspected
public record LoginResponseDTO(UUID token, UUID userId) {
    /**
     * Creates a body for the transaction.
     *
     * @param token the token of the user session
     * @param userId the id of the user
     */
    public LoginResponseDTO {
        requireNonNull(token);
        requireNonNull(userId);
    }
}
