package com.kalia.friday.user;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;

/**
 * Represents the body of a user delete transaction from the client to the server.
 */
@Introspected
public record UserDeleteDTO(@NotBlank String password) {
}
