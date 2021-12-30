package com.kalia.friday.util;

import biweekly.util.ByDay;
import biweekly.util.DayOfWeek;
import biweekly.util.Frequency;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BiWeeklyUtilsTest {

    @Test
    public void shouldThrowWithNullParam() {
        assertThrows(NullPointerException.class, () -> BiweeklyUtils.recurrenceRuleFromString(null));
    }

    @Test
    public void shouldThrowWithoutFrequency() {
        assertThrows(IllegalArgumentException.class, () -> BiweeklyUtils.recurrenceRuleFromString(""));
    }

    @Test
    public void shouldThrowWithInvalidFrequency() {
        assertThrows(IllegalArgumentException.class, () -> BiweeklyUtils.recurrenceRuleFromString("FREQ=abc"));
    }

    @Test
    public void successfulParse() {
        var recRule = BiweeklyUtils
            .recurrenceRuleFromString("FREQ=WEEKLY;INTERVAL=2;BYDAY=TU,WE,TH")
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
