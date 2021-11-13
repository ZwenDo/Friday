package com.kalia.friday.user;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Represents the body of a user password update transaction from the client to the server.
 */
@Introspected
public record UserPasswordUpdateDTO(
    @NotNull @NotBlank String oldPassword,
    @NotNull @NotBlank String newPassword) {}
