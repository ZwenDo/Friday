package com.kalia.friday.event;

import biweekly.util.ByDay;
import biweekly.util.DayOfWeek;
import biweekly.util.Frequency;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventRecurRulePartsTest {

    @Test
    public void shouldThrowWithNullParam() {
        assertThrows(NullPointerException.class, () -> EventRecurRuleParts.fromString(null));
    }

    @Test
    public void shouldThrowWithoutFrequency() {
        assertThrows(IllegalArgumentException.class, () -> EventRecurRuleParts.fromString(""));
    }

    @Test
    public void shouldThrowWithInvalidFrequency() {
        assertThrows(IllegalArgumentException.class, () -> EventRecurRuleParts.fromString("FREQ=abc"));
    }

    @Test
    public void successfulParse() {
        var recRule = EventRecurRuleParts
            .fromString("FREQ=WEEKLY;INTERVAL=2;BYDAY=TU,WE,TH")
            .getValue();
        assertEquals(Frequency.WEEKLY, recRule.getFrequency());
        assertEquals(2, recRule.getInterval());
        assertEquals(List.of(
            new ByDay(DayOfWeek.TUESDAY),
            new ByDay(DayOfWeek.WEDNESDAY),
            new ByDay(DayOfWeek.THURSDAY)
        ), recRule.getByDay());
    }
}
