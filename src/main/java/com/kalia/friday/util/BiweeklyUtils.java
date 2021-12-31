package com.kalia.friday.util;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.property.RecurrenceRule;
import biweekly.property.ValuedProperty;
import biweekly.util.ICalDate;
import biweekly.util.Recurrence;
import com.kalia.friday.event.EventDTO;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.kalia.friday.util.StringUtils.notBlankElse;
import static java.util.Objects.requireNonNull;

/**
 * Class containing Biweekly utility methods.
 */
public final class BiweeklyUtils {
    private static final String START = """
        BEGIN:VCALENDAR
        VERSION:2.0
        PRODID:-//Michael Angstadt//biweekly 0.6.6//EN
        BEGIN:VEVENT
        UID:74af3335-3084-43bb-83ae-0b766436d963
        DTSTAMP:19970324T120000Z
        RRULE:""";

    private static final String END = """

        END:VEVENT
        END:VCALENDAR""";


    private BiweeklyUtils() {
        throw new AssertionError("Cannot instantiate.");
    }

    /**
     * Creates a list of eventDTO from an ics formatted string.
     *
     * @param data   ics string
     * @param userId user id
     * @param token  user login token
     * @return the created event list
     */
    public static List<EventDTO> eventDTOListFromString(String data, UUID userId, UUID token) {
        requireNonNull(data);
        requireNonNull(userId);
        requireNonNull(token);
        return Biweekly.parse(data)
            .all()
            .stream()
            .flatMap(c -> c.getEvents().stream())
            .map(e -> vEventToEventDTO(e, userId, token))
            .toList();
    }

    private static EventDTO vEventToEventDTO(VEvent vEvent, UUID userId, UUID token) {
        var latitude = vEvent.getGeo() != null ? vEvent.getGeo().getLatitude() : null;
        var longitude = vEvent.getGeo() != null ? vEvent.getGeo().getLongitude() : null;
        return new EventDTO(
            userId,
            token,
            getPropValue(vEvent.getSummary()),
            notBlankElse(getPropValue(vEvent.getDescription()), null),
            notBlankElse(getPropValue(vEvent.getLocation()), null),
            recurrenceToString(getPropValue(vEvent.getRecurrenceRule())),
            fromICalDate(getPropValue(vEvent.getDateStart())),
            fromICalDate(getPropValue(vEvent.getDateEnd())),
            latitude,
            longitude
        );
    }

    /**
     * Creates a {@link RecurrenceRule} from a {@link String}.
     *
     * @param recurRulePart the string to create the rule.
     * @return the created recurrence rule
     */
    public static RecurrenceRule recurrenceRuleFromString(String recurRulePart) {
        requireNonNull(recurRulePart);
        return getFromString(recurRulePart);
    }

    /**
     * Asserts that a recurrence rule is valid.
     *
     * @param recurRulePart the rule to test
     * @throws IllegalArgumentException if rule is invalid
     */
    public static void requireValidRecurRule(String recurRulePart) {
        if (recurRulePart == null) return;
        getFromString(recurRulePart);
    }

    private static RecurrenceRule getFromString(String recurRulePart) {
        var cal = Biweekly.parse(START + recurRulePart + END).first();
        var opt = getRruleAsString(cal);
        if (opt.isPresent() && opt.get().contains("FREQ=")) {
            if (("RRULE:" + recurRulePart.trim()).length() != opt.get().trim().length()) {
                throw new IllegalArgumentException("Invalid recurrence rule: " + recurRulePart);
            }
        } else {
            throw new IllegalArgumentException("Invalid recurrence rule: " + recurRulePart);
        }
        return cal.getEvents().get(0).getRecurrenceRule();
    }

    private static Optional<String> getRruleAsString(ICalendar cal) {
        return Arrays.stream(Biweekly.write(cal).go().replaceAll("\r", "").split("\n"))
            .filter(s -> s.startsWith("RRULE:"))
            .findFirst();
    }

    private static String recurrenceToString(Recurrence recurrence) {
        if (recurrence == null) return null;
        var cal = new ICalendar();
        var event = new VEvent();
        event.setRecurrenceRule(recurrence);
        cal.addEvent(event);
        var opt = getRruleAsString(cal);
        if (opt.isEmpty()) {
            throw new AssertionError("No rule present.");
        }
        return opt.get();
    }

    private static LocalDateTime fromICalDate(ICalDate date) {
        return date == null ?
            null :
            Instant.ofEpochMilli(date.toInstant().toEpochMilli())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private static <T> T getPropValue(ValuedProperty<T> property) {
        return property != null ? property.getValue() : null;
    }
}
