package com.kalia.friday.event;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents the body of an event save request from the client to the server.
 */
@Introspected
public record EventDTO(
    @NotNull UUID userId,
    @NotNull UUID userToken,
    @NotEmpty String title,
    @NotBlank String description,
    @NotBlank String place,
    @NotBlank String recurRuleParts,
    @NotNull LocalDateTime startDate,
    Double latitude,
    Double longitude,
    long duration
) {
}
