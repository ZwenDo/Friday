package com.kalia.friday.util;

import static java.util.Objects.requireNonNull;

/**
 * Class that contains string utils methods.
 */
public final class StringUtils {
    private StringUtils() {}

    /**
     * Asserts that a string is not null and not blank.
     *
     * @param s the string to test
     * @return the string
     * @throws NullPointerException if string is null
     * @throws IllegalArgumentException if string is blank
     */
    public static String requireNotEmpty(String s) {
        requireNonNull(s);
        return requireNotBlank(s);
    }

    /**
     * Asserts that a string is not blank (it could be null).
     *
     * @param s the string to test
     * @return the string
     * @throws IllegalArgumentException if string is blank
     */
    public static String requireNotBlank(String s) {
        if (s != null && s.isEmpty()) {
            throw new IllegalArgumentException("string is empty");
        }
        return s;
    }
}
