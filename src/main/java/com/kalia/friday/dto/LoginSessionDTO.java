package com.kalia.friday.dto;

import io.micronaut.core.annotation.Introspected;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * Represents the body of a login session from.
 */
@Introspected
public record LoginSessionDTO(UUID userId, UUID token) {
    /**
     * Creates a body for the transaction.
     *
     * @param userId the id of the user
     * @param token the token of the user session
     */
    public LoginSessionDTO {
        requireNonNull(userId);
        requireNonNull(token);
    }
}
