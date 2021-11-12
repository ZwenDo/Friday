package com.kalia.friday.dto;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * Represents the body of a user delete transaction from the client to the server.
 */
@Introspected
public record UserDeleteDTO(@NotNull String password) {
    /**
     * Creates a body for the transaction.
     *
     * @param password the password of the user to delete.
     */
    public UserDeleteDTO {
        requireNonNull(password);
    }
}
