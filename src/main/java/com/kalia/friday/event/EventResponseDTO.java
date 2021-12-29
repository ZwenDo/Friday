package com.kalia.friday.event;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents the body of an event response from the server to the client.
 */
@Introspected
public record EventResponseDTO(
    @NotNull UUID id,
    @NotBlank String title,
    @Size(min = 1) String description,
    @Size(min = 1) String place,
    @Size(min = 1) String rrule,
    @NotNull LocalDateTime start,
    Double latitude,
    Double longitude,
    @NotNull LocalDateTime end
) {
}
