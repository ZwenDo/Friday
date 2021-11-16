package com.kalia.friday.login;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Represents the body of a login session from.
 */
@Introspected
public record LoginSessionDTO(
    @NotNull UUID userId,
    @NotNull UUID token
) {
}
