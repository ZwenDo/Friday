package com.kalia.friday.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StringUtilsTest {
    @Test
    public void testRequireNotBlankSuccessful() {
        assertEquals("abc", StringUtils.requireNotBlank("abc"));
        assertNull(StringUtils.requireNotBlank(null));
    }

    @Test
    public void testRequireNotEmptySuccessful() {
        assertEquals("abc", StringUtils.requireNotEmpty("abc"));
    }

    @Test
    public void testRequireNotBlankThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> StringUtils.requireNotBlank(""));
    }

    @Test
    public void testRequireNotEmptyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> StringUtils.requireNotEmpty(""));
        assertThrows(NullPointerException.class, () -> StringUtils.requireNotEmpty(null));
    }
}
