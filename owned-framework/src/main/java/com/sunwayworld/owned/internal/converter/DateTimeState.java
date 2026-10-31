package com.sunwayworld.owned.internal.converter;

import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

/** Project-owned formatter state replacing the supplier utility without exposing a compatibility facade. */
public final class DateTimeState {
    private static final ThreadLocal<SimpleDateFormat> DATETIME = dates("yyyy-MM-dd HH:mm:ss");
    private static final ThreadLocal<SimpleDateFormat> DATE = dates("yyyy-MM-dd");
    private static final ThreadLocal<SimpleDateFormat> TIME = dates("HH:mm:ss");
    private static final ThreadLocal<DateTimeFormatter> LOCAL_DATETIME = formatters("yyyy-MM-dd HH:mm:ss");
    private static final ThreadLocal<DateTimeFormatter> LOCAL_DATE = formatters("yyyy-MM-dd");
    private static final ThreadLocal<DateTimeFormatter> LOCAL_TIME = formatters("HH:mm:ss");
    private static final ThreadLocal<DateTimeFormatter> LOCAL_HOUR_MINUTE = formatters("HH:mm");
    private static final ThreadLocal<DateTimeFormatter> DATE_SERIAL = formatters("yyyyMMdd");
    private static ThreadLocal<SimpleDateFormat> dates(final String pattern) { return new ThreadLocal<SimpleDateFormat>() { protected SimpleDateFormat initialValue() { return new SimpleDateFormat(pattern); } }; }
    private static ThreadLocal<DateTimeFormatter> formatters(final String pattern) { return new ThreadLocal<DateTimeFormatter>() { protected DateTimeFormatter initialValue() { return DateTimeFormatter.ofPattern(pattern); } }; }
    private DateTimeState() {}
    public static String formatDateTime(Date value) { return value == null ? null : DATETIME.get().format(value); }
    public static String formatTime(Date value) { return value == null ? null : TIME.get().format(value); }
    public static String formatLocalDateTime(LocalDateTime value) { return value == null ? null : value.format(LOCAL_DATETIME.get()); }
    public static String formatLocalDate(LocalDate value) { return value == null ? null : value.format(LOCAL_DATE.get()); }
    public static String formatLocalTime(LocalTime value) { return value == null ? null : LOCAL_TIME.get().format(value); }
    public static Date parseDate(String source) {
        if (source == null || source.length() == 0) return null;
        String text = standard(source);
        try { return DATETIME.get().parse(text); } catch (ParseException ignored) {}
        if (text.indexOf('.') >= 0) {
            int fraction = text.length() - text.lastIndexOf('.') - 1;
            if (fraction > 0) try { return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss." + repeat('S', fraction)).parse(text); } catch (ParseException ignored) {}
        }
        try { return DATE.get().parse(text); } catch (ParseException failure) { throw new ConvertFailedException(String.class, Date.class, source); }
    }
    public static LocalDate parseLocalDate(String source) {
        if (source == null || source.length() == 0) return null;
        String text = standard(source);
        try { return LocalDate.parse(text, LOCAL_DATE.get()); } catch (DateTimeParseException ignored) {}
        try { return LocalDateTime.parse(text, LOCAL_DATETIME.get()).toLocalDate(); } catch (DateTimeParseException ignored) {}
        if (text.indexOf('.') >= 0) {
            int fraction = text.length() - text.lastIndexOf('.') - 1;
            if (fraction > 0) try { return LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss." + repeat('S', fraction))).toLocalDate(); } catch (Exception ignored) {}
        }
        try { return LocalDate.parse(text, DATE_SERIAL.get()); } catch (DateTimeParseException failure) { throw new ConvertFailedException(String.class, LocalDate.class, source); }
    }
    public static LocalDateTime parseLocalDateTime(String source) {
        if (source == null || source.length() == 0) return null;
        String text = standard(source);
        try { return LocalDateTime.parse(text, LOCAL_DATETIME.get()); } catch (DateTimeParseException ignored) {}
        try { return LocalDate.parse(text, LOCAL_DATE.get()).atStartOfDay(); } catch (DateTimeParseException ignored) {}
        if (text.indexOf('.') >= 0) {
            int fraction = text.length() - text.lastIndexOf('.') - 1;
            if (fraction > 0) try { return LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss." + repeat('S', fraction))); } catch (Exception ignored) {}
        }
        throw new ConvertFailedException(String.class, LocalDateTime.class, source);
    }
    public static LocalTime parseLocalTime(String source) {
        if (source == null || source.length() == 0) return null;
        try { return LocalTime.parse(source, LOCAL_TIME.get()); } catch (DateTimeParseException ignored) {}
        try { return LocalTime.parse(source, LOCAL_HOUR_MINUTE.get()); } catch (DateTimeParseException ignored) {}
        try { return LocalDateTime.parse(source, LOCAL_DATETIME.get()).toLocalTime(); } catch (DateTimeParseException ignored) {}
        if (source.indexOf('.') >= 0) {
            int fraction = source.length() - source.lastIndexOf('.') - 1;
            if (fraction > 0) try { return LocalDateTime.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss." + repeat('S', fraction))).toLocalTime(); } catch (DateTimeParseException ignored) {}
        }
        throw new ConvertFailedException(String.class, LocalTime.class, source);
    }
    private static String standard(String source) {
        String value = source;
        int offset = Math.max(value.lastIndexOf('-'), value.lastIndexOf('+'));
        if (offset >= "yyyy-M-d H:m:s".length()) value = value.substring(0, offset).trim();
        String[] parts = value.split("(-|/|年|月|日|:|T|t|\\.|\\s)");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i == 0) result.append(part); else if (i <= 2) result.append('-').append(leftPad(part, 2)); else if (i == 3) result.append(' ').append(leftPad(part, 2)); else if (i <= 5) result.append(':').append(leftPad(part, 2)); else if (i == 6) result.append('.').append(part);
        }
        if (result.length() == "yyyy-MM-dd HH:mm".length()) result.append(":00");
        return result.toString();
    }
    private static String leftPad(String value, int size) { StringBuilder b = new StringBuilder(); for (int i = value.length(); i < size; i++) b.append('0'); return b.append(value).toString(); }
    private static String repeat(char value, int count) { StringBuilder b = new StringBuilder(); for (int i = 0; i < count; i++) b.append(value); return b.toString(); }
}
