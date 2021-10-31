package com.kalia.friday.dto;

import io.micronaut.core.annotation.Introspected;

import static java.util.Objects.requireNonNull;

/**
 * Represents the body of a user credentials.
 */
@Introspected
public record UserCredsDTO(String username, String password) {
    /**
     * Creates a body for the transaction.
     *
     * @param username the name of the user
     * @param password the password of the user
     */
    public UserCredsDTO {
        requireNonNull(username);
        requireNonNull(password);
    }
}
