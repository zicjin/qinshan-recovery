package com.sunwayworld.framework.mybatis.mapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import com.sunwayworld.framework.converter.exception.ConverterNotFoundException;

/** Public filter operators retained from the business mapper contract. */
public enum MatchPattern {
    SB(String.class), SC(String.class), NSC(String.class), SE(String.class), SEQ(String.class),
    CISB(String.class), CISC(String.class), CISE(String.class), CISEQ(String.class),
    DG(LocalDate.class), DGOE(LocalDate.class), DL(LocalDate.class), DLOE(LocalDate.class),
    DEQ(LocalDate.class), DR(LocalDate.class), TG(LocalDateTime.class), TGOE(LocalDateTime.class),
    TL(LocalDateTime.class), TLOE(LocalDateTime.class), TEQ(LocalDateTime.class), TR(LocalDateTime.class),
    NG(Double.class), NGOE(Double.class), NL(Double.class), NLOE(Double.class), NEQ(Double.class), NR(Double.class),
    EQ(Void.class), DIFFER(Void.class), SPECIAL(String.class), MULTIPLE(String.class),
    NOR(Double.class), DOR(LocalDate.class), TOR(LocalDateTime.class), OR(String.class),
    ISNULL(Void.class), ISNOTNULL(Void.class), OSB(String.class), OSC(String.class), OSE(String.class),
    OCISB(String.class), OCISC(String.class), OCISE(String.class), OCISEQ(String.class),
    CBM(String.class), COBM(String.class), IN(String.class), OIN(String.class);

    private Class<?> type;

    MatchPattern(Class<?> type) { this.type = type; }

    @SuppressWarnings("unchecked")
    public <T> T parse(Object value) {
        if (Void.class.equals(type) || value == null) return (T) value;
        if (type.isInstance(value)) return (T) value;
        if (type == String.class) return (T) stringify(value);
        if (value instanceof String && ((String) value).isEmpty()) return null;
        if (type == Double.class) {
            if (!(value instanceof Number || value instanceof Boolean || value instanceof String)) throw new ConverterNotFoundException(value.getClass(), type);
            return (T) Double.valueOf(number(value));
        }
        if (type == LocalDate.class) return (T) toDate(value);
        if (type == LocalDateTime.class) return (T) toDateTime(value);
        if (type == LocalTime.class) return (T) toTime(value);
        throw new ConverterNotFoundException(value.getClass(), type);
    }

    public static final MatchPattern getPatternIfPossible(String pattern) {
        if (pattern == null || pattern.isEmpty()) return null;
        return Arrays.stream(values()).filter(p -> p.name().equals(pattern)).findAny().orElse(null);
    }

    public Class<?> getType() { return type; }

    private static String stringify(Object value) {
        if (value == null) return "null";
        if (value instanceof java.util.Collection<?> || value instanceof java.util.Map<?, ?>) return com.alibaba.fastjson.JSON.toJSONString(value);
        if (value instanceof java.util.Date) return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((java.util.Date) value);
        if (value instanceof LocalDateTime) return ((LocalDateTime) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (!(value instanceof String || value instanceof Number || value instanceof Boolean || value instanceof LocalDate || value instanceof LocalTime || value instanceof java.util.Map)) throw new ConverterNotFoundException(value.getClass(), String.class);
        return String.valueOf(value);
    }

    private static String number(Object value) {
        if (value instanceof Boolean) return ((Boolean) value) ? "1" : "0";
        return stringify(value).replace(",", "");
    }

    private static LocalDate toDate(Object value) {
        if (value instanceof Long) return Instant.ofEpochMilli(((Long) value).longValue()).atZone(ZoneId.systemDefault()).toLocalDate();
        if (value instanceof LocalDateTime) return ((LocalDateTime) value).toLocalDate();
        if (value instanceof LocalTime) return LocalDate.of(1970, 1, 1);
        if (value instanceof java.util.Date) return Instant.ofEpochMilli(((java.util.Date) value).getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        if (!(value instanceof String)) throw new ConverterNotFoundException(value.getClass(), LocalDate.class);
        String text = standardDateText(String.valueOf(value));
        for (DateTimeFormatter formatter : new DateTimeFormatter[]{DateTimeFormatter.ofPattern("yyyy-MM-dd"), DateTimeFormatter.ofPattern("yyyyMMdd")}) {
            try { return LocalDate.parse(text, formatter); } catch (DateTimeParseException ignored) { }
        }
        try { return parseDateTimeText(text).toLocalDate(); }
        catch (DateTimeParseException error) { throw new ConvertFailedException(String.class, LocalDate.class, (Object) text); }
    }

    private static LocalDateTime toDateTime(Object value) {
        if (value instanceof Long) return Instant.ofEpochMilli(((Long) value).longValue()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (value instanceof LocalDate) return ((LocalDate) value).atStartOfDay();
        if (value instanceof LocalTime) return LocalDate.of(1970, 1, 1).atTime((LocalTime) value);
        if (value instanceof java.util.Date) return Instant.ofEpochMilli(((java.util.Date) value).getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (!(value instanceof String)) throw new ConverterNotFoundException(value.getClass(), LocalDateTime.class);
        String text = standardDateText(String.valueOf(value));
        try { return parseDateTimeText(text); }
        catch (DateTimeParseException ignored) {
            try { return LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay(); }
            catch (DateTimeParseException error) { throw new ConvertFailedException(String.class, LocalDateTime.class, (Object) value); }
        }
    }

    private static LocalDateTime parseDateTimeText(String text) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        int dot = text.lastIndexOf('.');
        if (dot >= 0) { StringBuilder p = new StringBuilder(pattern).append('.'); for (int i = dot + 1; i < text.length(); i++) p.append('S'); pattern = p.toString(); }
        return LocalDateTime.parse(text, DateTimeFormatter.ofPattern(pattern));
    }

    private static String standardDateText(String text) {
        int zone = Math.max(text.lastIndexOf('-'), text.lastIndexOf('+'));
        if (zone >= "yyyy-M-d H:m:s".length()) text = text.substring(0, zone).trim();
        String[] parts = text.split("(-|\\/|年|月|日|:|T|t|\\.|\\s)");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i == 0) result.append(parts[i]);
            else if (i <= 2) result.append('-').append(pad(parts[i]));
            else if (i == 3) result.append(' ').append(pad(parts[i]));
            else if (i <= 5) result.append(':').append(pad(parts[i]));
            else if (i == 6) result.append('.').append(parts[i]);
        }
        if (result.length() == 16) result.append(":00");
        return result.toString();
    }

    private static String pad(String text) { return text.length() < 2 ? "0" + text : text; }
    private static LocalTime toTime(Object value) { return LocalTime.parse(String.valueOf(value)); }
}
