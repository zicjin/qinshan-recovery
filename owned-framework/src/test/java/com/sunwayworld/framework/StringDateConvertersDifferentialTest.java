package com.sunwayworld.framework;

import java.io.File;
import java.lang.reflect.*;
import java.net.*;
import java.time.*;
import java.time.temporal.Temporal;
import java.util.*;

/** Public date converter seams; supplier is reference-only in a separate loader. */
public final class StringDateConvertersDifferentialTest {
    private static final String[] INPUTS = {null, "", " ", "invalid", "2024-02-29", "2024/2/9 1:2",
        "2024-02-29 01:02", "2024-02-29 01:02:03.1", "2024-02-29 01:02:03.123",
        "2024-02-29 01:02:03.123456789", "2024-02-29 01:02:03.1234567890",
        "2024-02-29T01:02:03.123", "2024-02-29T01:02:03.123+08:00", "2024-02-29T01:02:03-05:00",
        "2024年2月29日 1:2:3", "20240229", "20240229010203", "2023-02-29", "2024-02-30",
        "2024-13-32", "2024-00-00", "0000-01-01", "0001-01-01", "9999-12-31", "2024-1-2",
        " 2024-01-02", "2024-01-02 ", "2024-01-02  01:02:03", "2021-03-14 02:30:00",
        "2021-11-07 01:30:00", "01:02", "01:02:03", "01:02:03.123", "24:00", "24:00:00",
        "23:59:60", "1:2:3", "2024-02-29 24:00:00", "2024-02-29 01:02:03.",
        "2024-02-29 01:02:03.123Z", "2024.2.29"};
    public static class BoxedDate extends Date { public BoxedDate(Long value) { super(value); } }
    public static class ThrowingDate extends Date { public ThrowingDate(Long value) { throw new IllegalArgumentException("fixture"); } }
    public abstract static class AbstractDate extends Date { public AbstractDate(Long value) { super(value); } }
    public static class PrimitiveDate extends Date { public PrimitiveDate(long value) { super(value); } }
    private static URLClassLoader loader(File dependencies, File framework) throws Exception {
        List<URL> urls = new ArrayList<URL>(); urls.add(framework.toURI().toURL());
        for (File f : dependencies.listFiles()) if (f.getName().endsWith(".jar")) urls.add(f.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[0]), null);
    }
    private static String value(Object value) {
        if (value == null) return "null";
        if (value instanceof Date) return value.getClass().getName() + ":" + ((Date) value).getTime();
        return value.getClass().getName() + ":" + value;
    }
    private static String invoke(Object receiver, Method method, Object... arguments) throws Exception {
        method.setAccessible(true);
        try { return value(method.invoke(receiver, arguments)); }
        catch (InvocationTargetException failure) {
            return describe(failure.getCause());
        }
    }
    private static String describe(Throwable failure) {
        return "ERROR:" + failure.getClass().getName() + ":" + failure.getMessage() +
            (failure.getCause() == null ? "" : ":CAUSE:" + describe(failure.getCause()));
    }
    private static String invokeConvert(Object converter, Class<?> sourceType, Object source) throws Exception {
        return invoke(converter, converter.getClass().getMethod("convert", sourceType), source);
    }
    private static List<String> observe(ClassLoader loader) throws Exception {
        List<String> out = new ArrayList<String>();
        Class<?> dateFactoryClass = loader.loadClass("com.sunwayworld.framework.converter.string.StringToDateConverterFactory");
        Object dateFactory = dateFactoryClass.getField("INSTANCE").get(null);
        for (Class<?> target : new Class<?>[] {Date.class, java.sql.Date.class, java.sql.Time.class, java.sql.Timestamp.class, BoxedDate.class, ThrowingDate.class, AbstractDate.class, PrimitiveDate.class, Object.class, null}) {
            Object converter = dateFactoryClass.getMethod("getConverter", Class.class).invoke(dateFactory, target);
            Class<?> type = converter.getClass();
            out.add("date:" + target + " source=" + invoke(converter, type.getMethod("getSourceType")) + " target=" + invoke(converter, type.getMethod("getTargetType")));
            for (String source : INPUTS)
                out.add("date:" + target + ":" + source + "=" + invokeConvert(converter, String.class, source));
        }
        Class<?> jsrFactoryClass = loader.loadClass("com.sunwayworld.framework.converter.string.StringToJSR310ConverterFactory");
        Object jsrFactory = jsrFactoryClass.getField("INSTANCE").get(null);
        for (Class<?> target : new Class<?>[] {LocalDateTime.class, LocalDate.class, LocalTime.class, Instant.class, null}) {
            Object converter = jsrFactoryClass.getMethod("getConverter", Class.class).invoke(jsrFactory, target);
            Class<?> type = converter.getClass();
            out.add("jsr:" + target + " source=" + invoke(converter, type.getMethod("getSourceType")) + " target=" + invoke(converter, type.getMethod("getTargetType")));
            for (String source : INPUTS)
                out.add("jsr:" + target + ":" + source + "=" + invokeConvert(converter, String.class, source));
        }
        Class<?> dateToString = loader.loadClass("com.sunwayworld.framework.converter.date.DateToStringConverterFactory$DateToStringConverter");
        Class<?> timeToString = loader.loadClass("com.sunwayworld.framework.converter.date.DateToStringConverterFactory$TimeToStringConverter");
        for (Class<?> type : new Class<?>[] {dateToString, timeToString}) {
            Object converter = type.getField("INSTANCE").get(null);
            for (Date value : new Date[] {null, new Date(0L), new Date(1709168523123L), new java.sql.Time(3723000L)})
                out.add("date-to-string:" + type.getName() + ":" + value(value) + "=" + invokeConvert(converter, Date.class, value));
        }
        for (String name : new String[] {"LocalDateTimeToStringConverter", "LocalDateToStringConverter", "LocalTimeToStringConverter"}) {
            Class<?> type = loader.loadClass("com.sunwayworld.framework.converter.date.JSR310ToStringGenericConverter$" + name);
            Object converter = type.getField("INSTANCE").get(null);
            Object[] values = name.startsWith("LocalDateTime") ? new Object[] {null, LocalDateTime.of(2024,2,29,1,2,3,123000000)} : name.startsWith("LocalDateTo") ? new Object[] {null, LocalDate.of(2024,2,29)} : new Object[] {null, LocalTime.of(1,2,3,123000000)};
            for (Object value : values) out.add("jsr-to-string:" + type.getName() + ":" + value(value) + "=" + invokeConvert(converter, Temporal.class, value));
        }
        TimeZone originalZone = TimeZone.getDefault();
        Locale originalLocale = Locale.getDefault();
        try {
            Object formatter = dateToString.getField("INSTANCE").get(null);
            Object parser = dateFactoryClass.getMethod("getConverter", Class.class).invoke(dateFactory, BoxedDate.class);
            TimeZone.setDefault(TimeZone.getTimeZone("Pacific/Honolulu"));
            Locale.setDefault(Locale.JAPAN);
            out.add("changed-zone-format=" + invokeConvert(formatter, Date.class, new Date(0L)));
            out.add("changed-zone-parse=" + invokeConvert(parser, String.class, "2024-02-29 01:02:03"));
        } finally { TimeZone.setDefault(originalZone); Locale.setDefault(originalLocale); }
        return out;
    }
    private static List<String> observeThreadLifecycle(final ClassLoader loader) throws Exception {
        final List<String> result = Collections.synchronizedList(new ArrayList<String>());
        final java.util.concurrent.CountDownLatch primed = new java.util.concurrent.CountDownLatch(1);
        final java.util.concurrent.CountDownLatch changed = new java.util.concurrent.CountDownLatch(1);
        final java.util.concurrent.atomic.AtomicReference<Throwable> failure = new java.util.concurrent.atomic.AtomicReference<Throwable>();
        TimeZone originalZone = TimeZone.getDefault();
        Locale originalLocale = Locale.getDefault();
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale.setDefault(Locale.US);
            final Class<?> type = loader.loadClass("com.sunwayworld.framework.converter.date.DateToStringConverterFactory$DateToStringConverter");
            final Object converter = type.getField("INSTANCE").get(null);
            Thread existing = new Thread(new Runnable() { public void run() {
                try {
                    result.add("existing-initial=" + invokeConvert(converter, Date.class, new Date(0L)));
                    primed.countDown();
                    if (!changed.await(10, java.util.concurrent.TimeUnit.SECONDS)) throw new AssertionError("defaults mutation timed out");
                    result.add("existing-after=" + invokeConvert(converter, Date.class, new Date(0L)));
                } catch (Throwable error) { failure.set(error); primed.countDown(); }
            }}, "date-contract-existing");
            existing.start();
            if (!primed.await(10, java.util.concurrent.TimeUnit.SECONDS)) throw new AssertionError("formatter prime timed out");
            TimeZone.setDefault(TimeZone.getTimeZone("Pacific/Honolulu"));
            Locale.setDefault(new Locale("th", "TH"));
            changed.countDown();
            existing.join(10000);
            if (existing.isAlive()) throw new AssertionError("existing formatter thread did not finish");
            Thread fresh = new Thread(new Runnable() { public void run() {
                try { result.add("fresh-after=" + invokeConvert(converter, Date.class, new Date(0L))); }
                catch (Throwable error) { failure.set(error); }
            }}, "date-contract-fresh");
            fresh.start(); fresh.join(10000);
            if (fresh.isAlive()) throw new AssertionError("fresh formatter thread did not finish");
            if (failure.get() != null) throw new AssertionError(failure.get());
            return result;
        } finally { changed.countDown(); TimeZone.setDefault(originalZone); Locale.setDefault(originalLocale); }
    }
    public static void main(String[] args) throws Exception {
        try (URLClassLoader reference = loader(new File(args[0]), new File(args[1])); URLClassLoader owned = loader(new File(args[2]), new File(args[3]))) {
            List<String> expected = observe(reference), actual = observe(owned);
            expected.addAll(observeThreadLifecycle(reference)); actual.addAll(observeThreadLifecycle(owned));
            if (!expected.equals(actual)) { for (int i = 0; i < Math.min(expected.size(), actual.size()); i++) if (!expected.get(i).equals(actual.get(i))) throw new AssertionError("at " + i + " supplier=" + expected.get(i) + " owned=" + actual.get(i)); throw new AssertionError("observation count supplier=" + expected.size() + " owned=" + actual.size()); }
            System.out.println("string/date converters differential: PASS (" + expected.size() + " observations)");
        }
    }
}
