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

    @Test
    public void testNotBlankElseWithBlank() {
        assertEquals("foo", StringUtils.notBlankElse("", "foo"));
    }
    @Test
    public void testNotBlankElseWithNull() {
        assertNull(StringUtils.notBlankElse(null, "foo"));
    }

    @Test
    public void testNotBlankElseWithNotBlank() {
        assertEquals("bar", StringUtils.notBlankElse("bar", "foo"));
    }

    @Test
    public void testNotNullOrBlankElseWithBlank() {
        assertEquals("foo", StringUtils.notNullOrBlankElse("", "foo"));
    }

    @Test
    public void testNotNullOrBlankElseWithNull() {
        assertEquals("foo", StringUtils.notNullOrBlankElse(null, "foo"));
    }

    @Test
    public void testNotNullOrBlankElseWithNotBlank() {
        assertEquals("bar", StringUtils.notNullOrBlankElse("bar", "foo"));
    }
}
