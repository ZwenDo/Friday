package com.kalia.friday.event;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;
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
    @Size(min = 1) String recurRuleParts,
    @NotNull LocalDateTime startDate,
    Double latitude,
    Double longitude,
    @NotNull LocalDateTime endDate
) {

    public static EventResponseDTO fromEvent(Event event) {
        Objects.requireNonNull(event);
        return new EventResponseDTO(
            event.id(),
            event.title(),
            event.description(),
            event.place(),
            event.recurRuleParts(),
            event.startDate(),
            event.latitude(),
            event.longitude(),
            event.endDate()
        );
    }
}
