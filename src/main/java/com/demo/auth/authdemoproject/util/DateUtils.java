package com.demo.auth.authdemoproject.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

public final class DateUtils {

    private DateUtils() {
        throw new IllegalStateException("Date Utils Class");
    }

    public static LocalDateTime getLocalDateTime() {
        return LocalDateTime.now();
    }

    public static Date getLocalDate() {
        return new Date();
    }

    public static Instant getInstant() {
        return Instant.now();
    }
}
