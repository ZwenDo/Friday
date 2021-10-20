package com.notkamui.dto;

import io.micronaut.core.annotation.Introspected;

import static java.util.Objects.requireNonNull;

/**
 * Represents the body of a user password update transaction from the client to the server.
 */
@Introspected
public record UserPasswordUpdateDTO(String oldPassword, String newPassword) {
    /**
     * Creates a body for the transaction.
     *
     * @param oldPassword the old password of the user to update
     * @param newPassword the new password to give to the user
     */
    public UserPasswordUpdateDTO {
        requireNonNull(oldPassword);
        requireNonNull(newPassword);
    }
}
