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
import java.time.Year;
import java.time.YearMonth;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.chrono.JapaneseDate;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.io.Serializable;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class JSR310BridgeDifferentialTest {
    public static final class NumberTemporal extends Number implements Temporal {
        public int intValue() { return 123; }
        public long longValue() { return 1615707000123L; }
        public float floatValue() { return 123F; }
        public double doubleValue() { return 123D; }
        public boolean isSupported(TemporalField field) { return false; }
        public boolean isSupported(TemporalUnit unit) { return false; }
        public long getLong(TemporalField field) { throw new UnsupportedOperationException(); }
        public Temporal with(TemporalField field, long value) { throw new UnsupportedOperationException(); }
        public Temporal plus(long amount, TemporalUnit unit) { throw new UnsupportedOperationException(); }
        public long until(Temporal end, TemporalUnit unit) { throw new UnsupportedOperationException(); }
    }

    private static String describe(Throwable failure) {
        return "!" + failure.getClass().getName() + ":" + String.valueOf(failure.getMessage())
                + (failure.getCause() == null ? "" : ":cause=" + describe(failure.getCause()));
    }

    private static String invoke(Method method, Object receiver, Object... arguments) throws Exception {
        try {
            Object value = method.invoke(receiver, arguments);
            return value == null ? "null" : value.getClass().getName() + ":" + value;
        } catch (InvocationTargetException failure) {
            return describe(failure.getCause());
        }
    }

    private static URLClassLoader loader(File artifact, File dependencies, File owned) throws Exception {
        List<URL> urls = new ArrayList<URL>();
        if (artifact != null) urls.add(artifact.toURI().toURL());
        if (dependencies != null) {
            File[] jars = dependencies.listFiles();
            if (jars != null) for (File jar : jars)
                if (jar.getName().endsWith(".jar")) urls.add(jar.toURI().toURL());
        }
        if (owned != null) urls.add(owned.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), null);
    }

    private static List<String> observe(ClassLoader loader) throws Exception {
        List<String> result = new ArrayList<String>();
        Class<?> factoryType = loader.loadClass("com.sunwayworld.framework.converter.date.JSR310ToJSR310ConverterFactory");
        Object factory = factoryType.getField("INSTANCE").get(null);
        Method values = factoryType.getMethod("values");
        Method getConverter = factoryType.getMethod("getConverter", Class.class);
        Class<?> converterType = loader.loadClass("com.sunwayworld.framework.converter.Converter");
        Method convert = converterType.getMethod("convert", Object.class);
        Method source = converterType.getMethod("getSourceType");
        Method target = converterType.getMethod("getTargetType");
        Method canConvert = converterType.getMethod("canConvert", Class.class, Class.class);
        result.add("values=" + Arrays.toString((Object[]) values.invoke(null)));
        Class<?>[] targets = new Class<?>[] {LocalDate.class, LocalDateTime.class, LocalTime.class,
                Temporal.class, Instant.class, Year.class, JapaneseDate.class, Object.class,
                Serializable.class, Comparable.class, Long.class, long.class, Number.class,
                String.class, CharSequence.class, Boolean.class, boolean.class, Integer.class, int.class,
                Byte.class, byte.class, Character.class, char.class, Double.class, double.class,
                BigInteger.class, BigDecimal.class, NumberTemporal.class, java.util.Date.class, void.class, null};
        Object[] inputs = new Object[] {
                null,
                LocalDate.of(2020, 2, 29),
                LocalDate.of(1970, 1, 1),
                LocalDateTime.of(2021, 3, 14, 2, 30, 42, 987654321),
                LocalDateTime.of(2021, 11, 7, 1, 30, 42, 987654321),
                LocalDateTime.of(1969, 12, 31, 23, 59, 59, 999999999),
                LocalTime.of(23, 59, 58, 987654321),
                LocalTime.MIDNIGHT,
                LocalTime.NOON,
                Instant.ofEpochMilli(1615707000123L),
                Year.of(2024),
                YearMonth.of(2024, 2),
                OffsetDateTime.of(2020, 2, 29, 12, 30, 42, 987654321, ZoneOffset.UTC),
                ZonedDateTime.of(2020, 2, 29, 12, 30, 42, 987654321, ZoneOffset.UTC),
                JapaneseDate.of(2020, 2, 29),
                LocalDate.MIN, LocalDate.MAX, LocalDateTime.MIN, LocalDateTime.MAX,
                new NumberTemporal(),
                "wrong"
        };
        for (Class<?> targetType : targets) {
            Object converter = getConverter.invoke(factory, targetType);
            result.add("target=" + targetType);
            result.add("source=" + source.invoke(converter));
            result.add("target-type=" + target.invoke(converter));
            result.add("fresh=" + (converter != getConverter.invoke(factory, targetType)));
            for (Object input : inputs) result.add("convert=" + invoke(convert, converter, input));
            for (Class<?> sourceType : new Class<?>[] {null, LocalDate.class, LocalDateTime.class,
                    LocalTime.class, Instant.class, Temporal.class, Object.class})
                result.add("can=" + invoke(canConvert, converter, sourceType, targetType));
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        try (URLClassLoader supplier = loader(new File(args[0]), new File(args[2]), null);
             URLClassLoader owned = loader(null, new File(args[3]), new File(args[1]))) {
            List<String> expected = observe(supplier);
            List<String> actual = observe(owned);
            if (expected.size() != actual.size()) throw new AssertionError("observation count differs");
            for (int i = 0; i < expected.size(); i++)
                if (!expected.get(i).equals(actual.get(i)))
                    throw new AssertionError("observation " + i + " supplier=" + expected.get(i) + " owned=" + actual.get(i));
            System.out.println("JSR310 bridge differential: PASS (" + expected.size() + " observations)");
        }
    }
}
