package com.kalia.friday.googlecalendar;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Represents the body of a Google calendar import request from the client to the server.
 */
@Introspected
public record GCalImportDTO(
    @NotNull UUID userId,
    @NotNull UUID token,
    @Email String googleId
) {
}
