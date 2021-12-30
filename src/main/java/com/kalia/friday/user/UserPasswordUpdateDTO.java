package com.kalia.friday.user;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;

/**
 * Represents the body of a user password update transaction from the client to the server.
 */
@Introspected
public record UserPasswordUpdateDTO(
    @NotBlank String oldPassword,
    @NotBlank String newPassword
) {
}
