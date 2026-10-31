package com.sunwayworld.framework;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class DateTimeUtilsDifferentialTest {
    private static String describe(Throwable failure) {
        return "!" + failure.getClass().getName() + ":" + String.valueOf(failure.getMessage())
                + (failure.getCause() == null ? "" : ":cause=" + describe(failure.getCause()));
    }

    private static String value(Object value) {
        if (value == null) return "null";
        if (value instanceof Date) return value.getClass().getName() + ":millis=" + ((Date) value).getTime();
        return value.getClass().getName() + ":" + value;
    }

    private static String invoke(Method method, Object... arguments) throws Exception {
        try { return value(method.invoke(null, arguments)); }
        catch (InvocationTargetException failure) { return describe(failure.getCause()); }
    }

    private static URLClassLoader loader(File artifact, File dependencies, File owned) throws Exception {
        List<URL> urls = new ArrayList<URL>();
        if (artifact != null) urls.add(artifact.toURI().toURL());
        if (dependencies != null) {
            File[] jars = dependencies.listFiles();
            if (jars != null) for (File jar : jars) if (jar.getName().endsWith(".jar")) urls.add(jar.toURI().toURL());
        }
        if (owned != null) urls.add(owned.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), null);
    }

    private static List<String> observe(ClassLoader loader) throws Exception {
        Class<?> type = loader.loadClass("com.sunwayworld.framework.utils.DateTimeUtils");
        List<String> result = new ArrayList<String>();
        Method serialDateTime = type.getMethod("getDateTimeSerialNumber", LocalDateTime.class);
        Method serialDate = type.getMethod("getDateSerialNumber", LocalDate.class);
        Method serialDateTimeAsDate = type.getMethod("getDateSerialNumber", LocalDateTime.class);
        Method formatDateTime = type.getMethod("formatDateTime", Date.class);
        Method formatDate = type.getMethod("formatDate", Date.class);
        Method formatTime = type.getMethod("formatTime", Date.class);
        Method formatLocalDateTime = type.getMethod("formatLocalDateTime", LocalDateTime.class);
        Method formatLocalDateTimePattern = type.getMethod("formatLocalDateTime", LocalDateTime.class, String.class);
        Method formatLocalDate = type.getMethod("formatLocalDate", LocalDate.class);
        Method formatLocalDatePattern = type.getMethod("formatLocalDate", LocalDate.class, String.class);
        Method formatLocalTime = type.getMethod("formatLocalTime", LocalTime.class);
        Method parseDate = type.getMethod("parseDate", String.class);
        Method parseLocalDate = type.getMethod("parseLocalDate", String.class);
        Method parseLocalDateTime = type.getMethod("parseLocalDateTime", String.class);
        Method parseLocalTime = type.getMethod("parseLocalTime", String.class);
        Method duration = type.getMethod("getDurationInMilliseconds", Temporal.class, Temporal.class);
        Method addDay = type.getMethod("addDay", Object.class, Integer.TYPE);
        Method plusYearsDateTime = type.getMethod("plusYears", LocalDateTime.class, Long.class);
        Method plusMonthsDateTime = type.getMethod("plusMonths", LocalDateTime.class, Long.class);
        Method plusWeeksDateTime = type.getMethod("plusWeeks", LocalDateTime.class, Long.class);
        Method plusDaysDateTime = type.getMethod("plusDays", LocalDateTime.class, Long.class);
        Method plusHoursDateTime = type.getMethod("plusHours", LocalDateTime.class, Long.class);
        Method plusMinutesDateTime = type.getMethod("plusMinutes", LocalDateTime.class, Long.class);
        Method plusYearsDate = type.getMethod("plusYears", LocalDate.class, Long.class);
        Method plusMonthsDate = type.getMethod("plusMonths", LocalDate.class, Long.class);
        Method plusWeeksDate = type.getMethod("plusWeeks", LocalDate.class, Long.class);
        Method plusDaysDate = type.getMethod("plusDays", LocalDate.class, Long.class);
        Method beforeDate = type.getMethod("isBefore", LocalDate.class, LocalDate.class);
        Method afterDate = type.getMethod("isAfter", LocalDate.class, LocalDate.class);
        Method equalDate = type.getMethod("isEqual", LocalDate.class, LocalDate.class);
        Method beforeDateTime = type.getMethod("isBefore", LocalDateTime.class, LocalDateTime.class);
        Method afterDateTime = type.getMethod("isAfter", LocalDateTime.class, LocalDateTime.class);
        Method equalDateTime = type.getMethod("isEqual", LocalDateTime.class, LocalDateTime.class);
        Method max = type.getMethod("max", LocalDateTime.class, LocalDateTime.class);
        Method min = type.getMethod("min", LocalDateTime.class, LocalDateTime.class);
        Method validDate = type.getMethod("isLocalDate", String.class);
        Method validDateTime = type.getMethod("isLocalDateTime", String.class);
        LocalDate date = LocalDate.of(2020, 2, 29);
        LocalDateTime dateTime = LocalDateTime.of(2021, 3, 14, 2, 30, 42, 987654321);
        LocalTime time = LocalTime.of(23, 59, 58, 987654321);
        Date instant = new Date(1615707000123L);
        for (Object input : new Object[] {date, null}) result.add("serial-date=" + invoke(serialDate, input));
        for (Object input : new Object[] {dateTime, null}) result.add("serial-datetime=" + invoke(serialDateTime, input));
        for (Object input : new Object[] {dateTime, null}) result.add("serial-date-from-datetime=" + invoke(serialDateTimeAsDate, input));
        for (Object input : new Object[] {instant, new Timestamp(1615707000123L), null}) {
            result.add("format-date-time=" + invoke(formatDateTime, input));
            result.add("format-date=" + invoke(formatDate, input));
            result.add("format-time=" + invoke(formatTime, input));
        }
        for (Object input : new Object[] {dateTime, null}) {
            result.add("format-local-datetime=" + invoke(formatLocalDateTime, input));
            for (String pattern : new String[] {null, "", "yyyy/MM/dd HH:mm", "yyyy-MM-dd'T'HH:mm:ss.SSS"})
                result.add("format-local-datetime-pattern=" + invoke(formatLocalDateTimePattern, input, pattern));
        }
        for (Object input : new Object[] {date, null}) {
            result.add("format-local-date=" + invoke(formatLocalDate, input));
            for (String pattern : new String[] {null, "", "yyyy/MM/dd", "yyyyMMdd"})
                result.add("format-local-date-pattern=" + invoke(formatLocalDatePattern, input, pattern));
        }
        for (Object input : new Object[] {time, null}) result.add("format-local-time=" + invoke(formatLocalTime, input));
        String[] texts = {null, "", " ", "2020-02-29", "2020-02-29 02:30:42", "2020/2/29 2:30", "2020年2月29日 2:30:42", "2020-02-29T02:30:42.123", "20200229", "2020-02-29 02:30:42.123456", "not-a-date"};
        for (String text : texts) {
            result.add("parse-date=" + invoke(parseDate, text));
            result.add("parse-local-date=" + invoke(parseLocalDate, text));
            result.add("parse-local-datetime=" + invoke(parseLocalDateTime, text));
            result.add("parse-local-time=" + invoke(parseLocalTime, text));
            result.add("valid-date=" + invoke(validDate, text));
            result.add("valid-datetime=" + invoke(validDateTime, text));
        }
        for (Object[] pair : new Object[][] {{dateTime, dateTime.plusDays(2)}, {date, date.plusDays(2)}, {time, time.plusHours(2)}, {Instant.EPOCH, Instant.ofEpochMilli(2)}, {dateTime, null}, {null, dateTime}})
            result.add("duration=" + invoke(duration, pair));
        for (Object input : new Object[] {instant, date, dateTime, time, null})
            for (int days : new int[] {-2, 0, 2}) result.add("add-day=" + invoke(addDay, input, days));
        for (Long amount : new Long[] {null, -2L, 0L, 2L}) {
            result.add("plus-years-datetime=" + invoke(plusYearsDateTime, dateTime, amount));
            result.add("plus-months-datetime=" + invoke(plusMonthsDateTime, dateTime, amount));
            result.add("plus-weeks-datetime=" + invoke(plusWeeksDateTime, dateTime, amount));
            result.add("plus-days-datetime=" + invoke(plusDaysDateTime, dateTime, amount));
            result.add("plus-hours-datetime=" + invoke(plusHoursDateTime, dateTime, amount));
            result.add("plus-minutes-datetime=" + invoke(plusMinutesDateTime, dateTime, amount));
            result.add("plus-years-date=" + invoke(plusYearsDate, date, amount));
            result.add("plus-months-date=" + invoke(plusMonthsDate, date, amount));
            result.add("plus-weeks-date=" + invoke(plusWeeksDate, date, amount));
            result.add("plus-days-date=" + invoke(plusDaysDate, date, amount));
        }
        for (Object[] pair : new Object[][] {{date, date.plusDays(1)}, {date, date.minusDays(1)}, {date, null}, {null, date}, {null, null}}) {
            result.add("before-date=" + invoke(beforeDate, pair)); result.add("after-date=" + invoke(afterDate, pair)); result.add("equal-date=" + invoke(equalDate, pair));
        }
        for (Object[] pair : new Object[][] {{dateTime, dateTime.plusDays(1)}, {dateTime, dateTime.minusDays(1)}, {dateTime, null}, {null, dateTime}, {null, null}}) {
            result.add("before-datetime=" + invoke(beforeDateTime, pair)); result.add("after-datetime=" + invoke(afterDateTime, pair)); result.add("equal-datetime=" + invoke(equalDateTime, pair)); result.add("max=" + invoke(max, pair)); result.add("min=" + invoke(min, pair));
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        Locale original = Locale.getDefault(); Locale.setDefault(Locale.US);
        try (URLClassLoader supplier = loader(new File(args[0]), new File(args[2]), null); URLClassLoader owned = loader(null, new File(args[3]), new File(args[1]))) {
            List<String> expected = observe(supplier); List<String> actual = observe(owned);
            if (!expected.equals(actual)) {
                for (int i = 0; i < Math.min(expected.size(), actual.size()); i++) if (!expected.get(i).equals(actual.get(i))) throw new AssertionError("observation " + i + " supplier=" + expected.get(i) + " owned=" + actual.get(i));
                throw new AssertionError("observation count supplier=" + expected.size() + " owned=" + actual.size());
            }
            System.out.println("DateTimeUtils differential: PASS (" + expected.size() + " observations)");
        } finally { Locale.setDefault(original); }
    }
}
