package com.notkamui.dto;

import io.micronaut.core.annotation.Introspected;

import static java.util.Objects.requireNonNull;

/**
 * Represents the body of a user creation transaction from the client to the server.
 */
@Introspected
public record UserSaveDTO(String username, String password) {
    /**
     * Creates a body for the transaction.
     *
     * @param username the name of the user to create
     * @param password the password of the user to create
     */
    public UserSaveDTO {
        requireNonNull(username);
        requireNonNull(password);
    }
}
