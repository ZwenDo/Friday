package com.kalia.friday.event;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents the body of an event save request from the client to the server.
 */
@Introspected
public record EventDTO(
    @NotNull UUID userId,
    @NotNull UUID userToken,
    @NotBlank String title,
    @Size(min = 1) String description,
    @Size(min = 1) String place,
    @Size(min = 1) String recurRuleParts,
    @NotNull LocalDateTime startDate,
    Double latitude,
    Double longitude,
    @NotNull LocalDateTime endDate
) {
}
