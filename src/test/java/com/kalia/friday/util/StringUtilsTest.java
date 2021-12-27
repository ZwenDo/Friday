package com.kalia.friday.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringUtilsTest {
    @Test
    public void testRequireNotBlankSuccessful() {
        assertEquals("abc", StringUtils.requireNotBlank("abc"));
        assertNull(StringUtils.requireNotBlank(null));
    }

    @Test
    public void testRequireNotEmptySuccessful() {
        assertEquals("abc", StringUtils.requireNotNullOrBlank("abc"));
    }

    @Test
    public void testRequireNotBlankThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> StringUtils.requireNotBlank(""));
    }

    @Test
    public void testRequireNotEmptyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> StringUtils.requireNotNullOrBlank(""));
        assertThrows(NullPointerException.class, () -> StringUtils.requireNotNullOrBlank(null));
    }
}
