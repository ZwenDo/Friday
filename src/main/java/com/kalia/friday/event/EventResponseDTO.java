package com.kalia.friday.event;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Represents the body of an event response from the server to the client.
 */
@Introspected
public record EventResponseDTO(
        @NotNull UUID id,
        @NotEmpty String title,
        @NotBlank String description,
        @NotBlank String place,
        @NotEmpty String recurRuleParts
) {}
