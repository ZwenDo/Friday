package com.kalia.friday.util;

import static java.util.Objects.requireNonNull;

public final class StringUtils {
    private StringUtils() {}

    public static String requireNotEmpty(String s) {
        requireNonNull(s);
        return requireNotBlank(s);
    }

    public static String requireNotBlank(String s) {
        if (s != null && s.isEmpty()) {
            throw new IllegalArgumentException("string is empty");
        }
        return s;
    }
}
