package com.sunwayworld.framework;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public final class DateConvertersDifferentialTest {
    public static final class DerivedSqlDate extends java.sql.Date {
        public DerivedSqlDate(long millis) { super(millis); }
    }

    public static final class MissingLongDate extends Date {
        public MissingLongDate() { super(); }
    }
    public static final class ThrowingDate extends Date {
        public ThrowingDate(long millis) { throw new IllegalStateException("synthetic-constructor-failure"); }
    }
    public abstract static class AbstractDate extends Date {
        public AbstractDate(long millis) { super(millis); }
    }

    private static String describe(Throwable failure) {
        return "!" + failure.getClass().getName() + ":" + failure.getMessage()
                + (failure.getCause() == null ? "" : ":cause=" + describe(failure.getCause()));
    }

    private static String invoke(Method method, Object receiver, Object... arguments) throws Exception {
        try {
            Object value = method.invoke(receiver, arguments);
            if (value instanceof Date) return value.getClass().getName() + ":millis=" + ((Date) value).getTime()
                    + (value instanceof java.sql.Timestamp ? ":nanos=" + ((java.sql.Timestamp) value).getNanos() : "");
            return value == null ? "null" : value.getClass().getName() + ":" + value;
        } catch (InvocationTargetException failure) {
            return describe(failure.getCause());
        }
    }

    private static URLClassLoader loader(File artifact, File deps, File testClasses) throws Exception {
        List<URL> urls = new ArrayList<URL>();
        if (artifact != null) urls.add(artifact.toURI().toURL());
        if (deps != null) {
            File[] jars = deps.listFiles();
            if (jars != null) for (File jar : jars) if (jar.getName().endsWith(".jar")) urls.add(jar.toURI().toURL());
        }
        if (testClasses != null) urls.add(testClasses.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), null);
    }

    private static List<String> observe(ClassLoader loader) throws Exception {
        List<String> result = new ArrayList<String>();
        Class<?> converter = loader.loadClass("com.sunwayworld.framework.converter.Converter");
        Method convert = converter.getMethod("convert", Object.class);
        Method source = converter.getMethod("getSourceType");
        Method target = converter.getMethod("getTargetType");
        Method canConvert = converter.getMethod("canConvert", Class.class, Class.class);
        for (String zone : new String[] {"UTC", "Asia/Shanghai", "America/New_York"}) {
            TimeZone.setDefault(TimeZone.getTimeZone(zone));
            result.add("zone=" + zone);
            for (String name : new String[] {"DateToJSR310ConverterFactory", "LongToJSR310ConverterFactory"}) {
                Class<?> factoryType = loader.loadClass("com.sunwayworld.framework.converter.date." + name);
                Object factory = factoryType.getField("INSTANCE").get(null);
                Method getConverter = factoryType.getMethod("getConverter", Class.class);
                result.add(name + ".values=" + Arrays.toString((Object[]) factoryType.getMethod("values").invoke(null)));
                for (Class<?> type : new Class<?>[] {LocalDate.class, LocalDateTime.class, LocalTime.class, Instant.class, Temporal.class, Object.class, null}) {
                    Object instance = getConverter.invoke(factory, type);
                    result.add(name + ".source=" + source.invoke(instance));
                    result.add(name + ".target=" + target.invoke(instance));
                    result.add(name + ".fresh=" + (instance != getConverter.invoke(factory, type)));
                    result.add(name + ".null=" + invoke(convert, instance, (Object) null));
                    for (long millis : new long[] {-1L, 0L, 123456789L, 1615707000123L, 1636266600123L, Long.MIN_VALUE, Long.MAX_VALUE}) {
                        Object input = name.startsWith("Date") ? (Object) new Date(millis) : Long.valueOf(millis);
                        result.add(name + ".convert=" + invoke(convert, instance, input));
                    }
                    result.add(name + ".wrong=" + invoke(convert, instance, "wrong"));
                    if (name.startsWith("Date")) {
                        for (Date date : new Date[] {new java.sql.Date(123456789L), new java.sql.Timestamp(123456789L), new java.sql.Time(123456789L), new DerivedSqlDate(123456789L)})
                            result.add(name + ".sql=" + date.getClass().getName() + ":" + invoke(convert, instance, date));
                    }
                    for (Class<?> sourceType : new Class<?>[] {null, Object.class, Date.class, java.sql.Date.class, Long.class})
                        result.add(name + ".can=" + invoke(canConvert, instance, sourceType, type));
                }
            }
            for (String prefix : new String[] {"LocalDate", "LocalDateTime", "LocalTime"}) {
                for (String suffix : new String[] {"Date", "SqlDate", "SqlTime", "SqlTimestamp"}) {
                    String name = prefix + "To" + suffix + "Converter";
                    Class<?> type = loader.loadClass("com.sunwayworld.framework.converter.date.JSR310ToDateGenericConverter$" + name);
                    Object instance = type.getField("INSTANCE").get(null);
                    result.add(name + ".values=" + Arrays.toString((Object[]) type.getMethod("values").invoke(null)));
                    result.add(name + ".source=" + source.invoke(instance));
                    result.add(name + ".target=" + target.invoke(instance));
                    result.add(name + ".null=" + invoke(convert, instance, (Object) null));
                    for (Object input : new Object[] {LocalDate.of(2020, 2, 29), LocalDateTime.of(2021, 3, 14, 2, 30), LocalDateTime.of(2021, 11, 7, 1, 30, 42, 987654321), LocalTime.of(23, 59, 58, 987654321), Instant.EPOCH, "wrong"})
                        result.add(name + ".convert=" + invoke(convert, instance, input));
                }
            }
            Class<?> fixture = loader.loadClass("com.sunwayworld.framework.DateConverterFixture");
            for (Class<?> sourceType : new Class<?>[] {LocalDateTime.class, LocalDate.class, LocalTime.class, Instant.class})
                for (Class<?> targetType : new Class<?>[] {Date.class, MissingLongDate.class, ThrowingDate.class, AbstractDate.class, null}) {
                    Object instance = fixture.getConstructor(Class.class, Class.class).newInstance(sourceType, targetType);
                    result.add("fixture.null=" + invoke(convert, instance, (Object) null));
                    for (Object input : new Object[] {LocalDate.of(2020, 2, 29), LocalDateTime.of(2021, 3, 14, 2, 30), LocalTime.NOON, Instant.EPOCH})
                        result.add("fixture.convert=" + invoke(convert, instance, input));
                }
            for (String name : new String[] {"LocalDateToLongConverter", "LocalDateTimeToLongConverter", "LocalTimeToLongConverter"}) {
                Class<?> type = loader.loadClass("com.sunwayworld.framework.converter.date.JSR310ToLongGenericConverter$" + name);
                Object instance = type.getField("INSTANCE").get(null);
                result.add(name + ".values=" + Arrays.toString((Object[]) type.getMethod("values").invoke(null)));
                result.add(name + ".source=" + source.invoke(instance));
                result.add(name + ".target=" + target.invoke(instance));
                result.add(name + ".null=" + invoke(convert, instance, (Object) null));
                for (Object input : new Object[] {LocalDate.of(2020, 2, 29), LocalDate.of(2021, 3, 14), LocalDateTime.of(2021, 3, 14, 2, 30), LocalDateTime.of(2021, 11, 7, 1, 30, 42, 987654321), LocalTime.of(23, 59, 58, 987654321), Instant.EPOCH, "wrong"})
                    result.add(name + ".convert=" + invoke(convert, instance, input));
            }
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        TimeZone original = TimeZone.getDefault();
        try (URLClassLoader supplier = loader(new File(args[0]), new File(args[3]), new File(args[2]));
             URLClassLoader owned = loader(new File(args[1]), new File(args[3]), new File(args[2]))) {
            List<String> expected = observe(supplier);
            List<String> actual = observe(owned);
            for (int i = 0; i < expected.size(); i++)
                if (!expected.get(i).equals(actual.get(i)))
                    throw new AssertionError("observation " + i + " supplier=" + expected.get(i) + " owned=" + actual.get(i));
            System.out.println("date converter differential: PASS (" + expected.size() + " observations)");
        } finally {
            TimeZone.setDefault(original);
        }
    }
}
