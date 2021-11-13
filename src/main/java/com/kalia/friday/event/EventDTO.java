package com.kalia.friday.event;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Represents the body of an event save request from the client to the server.
 */
@Introspected
public record EventDTO(
        @NotNull UUID userId,
        @NotNull UUID userToken,
        @NotNull @NotBlank String title,
        @NotBlank String description,
        @NotBlank String place,
        @NotNull @NotBlank String recurRuleParts
) {}
