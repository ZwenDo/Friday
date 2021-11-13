package com.kalia.friday.dto;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

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
) {
    /**
     * Creates a body for the save.
     *
     * @param userId the id of the user
     * @param userToken the token of the user
     * @param title the title of the event
     * @param description the description
     * @param place the place of the event
     * @param recurRuleParts the {@code recurRuleParts} of the event
     */
    public EventDTO {
        requireNonNull(userId);
        requireNonNull(userToken);
        requireNonNull(title);
        requireNonNull(recurRuleParts);
    }
}
