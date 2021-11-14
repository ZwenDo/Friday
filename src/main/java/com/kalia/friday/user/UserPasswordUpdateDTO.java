package com.kalia.friday.user;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotEmpty;

/**
 * Represents the body of a user password update transaction from the client to the server.
 */
@Introspected
public record UserPasswordUpdateDTO(
    @NotEmpty String oldPassword,
    @NotEmpty String newPassword
) {}
