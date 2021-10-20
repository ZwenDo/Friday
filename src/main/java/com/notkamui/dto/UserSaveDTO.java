package com.notkamui.dto;

import io.micronaut.core.annotation.Introspected;

import java.util.Objects;

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
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
    }
}
