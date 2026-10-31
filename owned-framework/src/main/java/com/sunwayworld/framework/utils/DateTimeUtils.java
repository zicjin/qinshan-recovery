package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import com.sunwayworld.owned.internal.converter.DateTimeState;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Date;

/** Date and Java-time helpers retained at the supplier-facing utility seam. */
public class DateTimeUtils {
    private static final DateTimeFormatter DATE_SERIAL = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DATETIME_SERIAL = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public DateTimeUtils() { }

    public static final String getDateTimeSerialNumber(LocalDateTime value) {
        return value.format(DATETIME_SERIAL);
    }

    public static final String getDateSerialNumber(LocalDate value) {
        return value.format(DATE_SERIAL);
    }

    public static final String getDateSerialNumber(LocalDateTime value) {
        return value.format(DATE_SERIAL);
    }

    public static final String formatLocalDateTime(LocalDateTime value, String pattern) {
        if (value == null) return null;
        return pattern == null || pattern.length() == 0 ? formatLocalDateTime(value) : value.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static final String formatLocalDateTime(LocalDateTime value) { return DateTimeState.formatLocalDateTime(value); }
    public static final String formatDateTime(Date value) { return DateTimeState.formatDateTime(value); }
    public static final String formatTime(Date value) { return DateTimeState.formatTime(value); }

    public static final String formatLocalDate(LocalDate value, String pattern) {
        if (value == null) return null;
        return pattern == null || pattern.length() == 0 ? formatLocalDate(value) : value.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static final String formatLocalDate(LocalDate value) { return DateTimeState.formatLocalDate(value); }
    public static final String formatDate(Date value) { return value == null ? null : DateTimeState.formatDateTime(value).substring(0, 10); }
    public static final String formatLocalTime(LocalTime value) { return DateTimeState.formatLocalTime(value); }
    public static final Date parseDate(String source) { return DateTimeState.parseDate(source); }
    public static final LocalDate parseLocalDate(String source) { return DateTimeState.parseLocalDate(source); }
    public static final LocalDateTime parseLocalDateTime(String source) { return DateTimeState.parseLocalDateTime(source); }
    public static final LocalTime parseLocalTime(String source) { return DateTimeState.parseLocalTime(source); }

    public static final long getDurationInMilliseconds(Temporal from, Temporal to) {
        Long end = ConvertUtils.convert(to, Long.class);
        Long start = ConvertUtils.convert(from, Long.class);
        return end.longValue() - start.longValue();
    }

    public static final <T> T addDay(T value, int days) {
        Date date = new Date(ConvertUtils.convert(value, Date.class).getTime() + 86400000L * (long) days);
        return ConvertUtils.convert(date, (Class<T>) value.getClass());
    }

    public static final LocalDateTime plusYears(LocalDateTime value, Long amount) { return value == null || amount == null ? null : value.plusYears(amount.longValue()); }
    public static final LocalDateTime plusMonths(LocalDateTime value, Long amount) { return value == null || amount == null ? null : value.plusMonths(amount.longValue()); }
    public static final LocalDateTime plusWeeks(LocalDateTime value, Long amount) { return value == null || amount == null ? null : value.plusWeeks(amount.longValue()); }
    public static final LocalDateTime plusDays(LocalDateTime value, Long amount) { return value == null || amount == null ? null : value.plusDays(amount.longValue()); }
    public static final LocalDateTime plusHours(LocalDateTime value, Long amount) { return value == null || amount == null ? null : value.plusHours(amount.longValue()); }
    public static final LocalDateTime plusMinutes(LocalDateTime value, Long amount) { return value == null || amount == null ? null : value.plusMinutes(amount.longValue()); }
    public static final LocalDate plusYears(LocalDate value, Long amount) { return value == null || amount == null ? null : value.plusYears(amount.longValue()); }
    public static final LocalDate plusMonths(LocalDate value, Long amount) { return value == null || amount == null ? null : value.plusMonths(amount.longValue()); }
    public static final LocalDate plusWeeks(LocalDate value, Long amount) { return value == null || amount == null ? null : value.plusWeeks(amount.longValue()); }
    public static final LocalDate plusDays(LocalDate value, Long amount) { return value == null || amount == null ? null : value.plusDays(amount.longValue()); }
    public static final boolean isBefore(LocalDate left, LocalDate right) { return left != null && right != null && left.isBefore(right); }
    public static final boolean isAfter(LocalDate left, LocalDate right) { return left != null && right != null && left.isAfter(right); }
    public static final boolean isEqual(LocalDate left, LocalDate right) { return left == null || right == null ? left == null && right == null : left.isEqual(right); }
    public static final boolean isBefore(LocalDateTime left, LocalDateTime right) { return left != null && right != null && left.isBefore(right); }
    public static final boolean isAfter(LocalDateTime left, LocalDateTime right) { return left != null && right != null && left.isAfter(right); }
    public static final boolean isEqual(LocalDateTime left, LocalDateTime right) { return left == null || right == null ? left == null && right == null : left.isEqual(right); }
    public static final LocalDateTime max(LocalDateTime left, LocalDateTime right) { return left == null ? right : right == null ? left : left.isAfter(right) ? left : right; }
    public static final LocalDateTime min(LocalDateTime left, LocalDateTime right) { return left == null ? right : right == null ? left : left.isBefore(right) ? left : right; }
    public static final boolean isLocalDate(String value) { try { parseLocalDate(value); return true; } catch (ConvertFailedException failure) { return false; } }
    public static final boolean isLocalDateTime(String value) { try { parseLocalDateTime(value); return true; } catch (ConvertFailedException failure) { return false; } }
}
