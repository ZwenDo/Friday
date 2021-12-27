package com.kalia.friday.event;

import biweekly.Biweekly;
import biweekly.property.RecurrenceRule;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;

/**
 * A class that contains recur rule parts util methods.
 */
public final class EventRecurRuleParts {
    private EventRecurRuleParts() {
        throw new AssertionError("Cannon instantiate.");
    }

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

    /**
     * Creates a {@link RecurrenceRule} from a {@link String}.
     *
     * @param recurRulePart the string to create the rule.
     * @return the created recurrence rule
     */
    public static RecurrenceRule fromString(String recurRulePart) {
        requireNonNull(recurRulePart);
        var cal = Biweekly.parse(START + recurRulePart + END).first();
        return cal.getEvents().get(0).getRecurrenceRule();
    }

    /**
     * Asserts that a recurrence rule is valid.
     *
     * @param recurRulePart the rule to test
     * @throws IllegalArgumentException if rule is invalid
     */
    public static void requireValidRecurRule(String recurRulePart) {
        requireNonNull(recurRulePart);
        var cal = Biweekly.parse(START + recurRulePart + END).first();
        var opt = Arrays.stream(Biweekly.write(cal).go().split("\n"))
            .filter(s -> s.startsWith("RRULE:"))
            .findFirst(); // gets the parsed rule
        if (opt.isPresent()) {
            if (!("RRULE:" + recurRulePart.trim()).equals(opt.get().trim())) {
                throw new IllegalArgumentException("Invalid recurrence rule: " + recurRulePart);
            }
        } else {
            throw new IllegalArgumentException("Invalid recurrence rule: " + recurRulePart);
        }
    }
}
