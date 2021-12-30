package com.kalia.friday.googlecalendar;

import com.google.api.services.calendar.model.EventDateTime;
import com.kalia.friday.event.EventDTO;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static com.kalia.friday.util.StringUtils.notBlankElse;
import static com.kalia.friday.util.StringUtils.notNullOrBlankElse;
import static java.util.Objects.requireNonNull;

/**
 * Class that contains util methods for Google calendar imports.
 */
public final class GoogleCalendars {
    private GoogleCalendars() {
        throw new AssertionError("Cannot instantiate.");
    }

    /**
     * Converts a list of Google calendar events to a list of Friday events.
     *
     * @param events list of Google calendar events
     * @param owner  the owner of the events
     * @return the list of converted Friday events
     */
    public static List<EventDTO> toFridayEventList(List<com.google.api.services.calendar.model.Event> events, UUID userId, UUID token) {
        requireNonNull(events);
        requireNonNull(userId);
        requireNonNull(token);
        return events.stream()
            .map(e -> toEventDTO(requireNonNull(e), userId, token))
            .toList();
    }

    private static EventDTO toEventDTO(com.google.api.services.calendar.model.Event event, UUID userId, UUID token) {
        return new EventDTO(
            userId,
            token,
            notNullOrBlankElse(event.getSummary(), "Untitled"),
            notBlankElse(event.getDescription(), null),
            event.getLocation(),
            getRrule(event.getRecurrence()),
            fromEventDateTime(event.getStart()),
            fromEventDateTime(event.getEnd()),
            null,
            null
        );
    }

    private static String getRrule(List<String> rules) {
        if (rules == null) return null;
        return rules.stream()
            .filter(s -> s.startsWith("RRULE"))
            .findFirst()
            .map(s -> s.replaceFirst("RRULE:", ""))
            .orElse(null);
    }

    private static LocalDateTime fromEventDateTime(EventDateTime date) {
        var actualDate = date.getDateTime() != null ? date.getDateTime() : date.getDate();
        return Instant.ofEpochMilli(actualDate.getValue())
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
    }

}
