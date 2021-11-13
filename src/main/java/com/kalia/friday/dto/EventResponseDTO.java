package com.kalia.friday.dto;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * Represents the body of an event response from the server to the client.
 */
@Introspected
public record EventResponseDTO(
        @NotNull UUID id,
        @NotNull @NotBlank String title,
        @NotBlank String description,
        @NotBlank String place,
        @NotNull @NotBlank String recurRuleParts
) {
    /**
     * Creates a body for the response
     *
     * @param id the created uuid of the event
     * @param title the title of the event
     * @param description the description of the event
     * @param place the place of the event
     * @param recurRuleParts the {@code recurRuleParts} of the event
     */
    public EventResponseDTO {
        requireNonNull(id);
        requireNonNull(title);
        requireNonNull(recurRuleParts);
    }
}
