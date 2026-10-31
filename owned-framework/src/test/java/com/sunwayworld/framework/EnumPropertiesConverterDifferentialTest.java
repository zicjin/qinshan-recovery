package com.sunwayworld.framework;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

public final class EnumPropertiesConverterDifferentialTest {
    private enum Mode { FIRST, SECOND }
    public static final class FailingProperties extends Properties {
        public void store(OutputStream output, String comments) throws IOException {
            throw new IOException("synthetic-store-failure");
        }
    }

    private static String describe(Throwable cause) {
        return "!" + cause.getClass().getName() + ":" + String.valueOf(cause.getMessage())
                + (cause.getCause() == null ? "" : ":cause=" + describe(cause.getCause()));
    }

    private static URLClassLoader loader(File supplier, File owned) throws Exception {
        List<URL> urls = new ArrayList<URL>();
        if (supplier != null) urls.add(supplier.toURI().toURL());
        if (owned != null) urls.add(owned.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), null);
    }

    private static String invoke(Method method, Object receiver, Object... args) throws Exception {
        try {
            Object value = method.invoke(receiver, args);
            if (value instanceof Properties) return "Properties:" + value;
            // Properties.store writes the current wall clock in its first comment line.
            // Require the standard header format, then compare the escaped body exactly.
            if (value instanceof String && ((String) value).startsWith("#")) {
                String text = (String) value;
                int end = text.indexOf('\n');
                if (end < 0 || !Pattern.matches("#[A-Za-z]{3} [A-Za-z]{3} .* [0-9]{4}\\r?", text.substring(0, end)))
                    throw new AssertionError("invalid Properties timestamp header: " + text);
                return "java.lang.String:#TIMESTAMP\n" + text.substring(end + 1);
            }
            return value == null ? "null" : value.getClass().getName() + ":" + value;
        } catch (InvocationTargetException failure) {
            return describe(failure.getCause());
        }
    }

    private static List<String> observe(ClassLoader loader) throws Exception {
        List<String> result = new ArrayList<String>();
        Class<?> enumFactoryType = loader.loadClass("com.sunwayworld.framework.converter.string.StringToEnumConverterFactory");
        Object enumFactory = enumFactoryType.getField("INSTANCE").get(null);
        result.add("enum-values=" + Arrays.toString((Object[]) enumFactoryType.getMethod("values").invoke(null)));
        Method getConverter = enumFactoryType.getMethod("getConverter", Class.class);
        Object enumConverter = getConverter.invoke(enumFactory, Mode.class);
        Class<?> converterType = loader.loadClass("com.sunwayworld.framework.converter.Converter");
        Method enumConvert = converterType.getMethod("convert", Object.class);
        Method enumSource = converterType.getMethod("getSourceType");
        Method enumTarget = converterType.getMethod("getTargetType");
        result.add("enum-source=" + enumSource.invoke(enumConverter));
        result.add("enum-target=" + enumTarget.invoke(enumConverter));
        for (Object value : new Object[] {null, "", " FIRST ", "SECOND", "first", "missing", " ", "\tSECOND\r\n", 1})
            result.add("enum-convert=" + invoke(enumConvert, enumConverter, value));
        result.add("enum-fresh=" + (enumConverter != getConverter.invoke(enumFactory, Mode.class)));
        Method canConvert = converterType.getMethod("canConvert", Class.class, Class.class);
        for (Class<?> source : new Class<?>[] {null, String.class, Object.class, Integer.class})
            for (Class<?> target : new Class<?>[] {null, Mode.class, Enum.class, Object.class, String.class})
                result.add("enum-can=" + invoke(canConvert, enumConverter, source, target));
        for (Class<?> target : new Class<?>[] {null, String.class, Enum.class}) {
            Object converter = getConverter.invoke(enumFactory, target);
            result.add("enum-other-target=" + enumTarget.invoke(converter));
            result.add("enum-other-convert=" + invoke(enumConvert, converter, "FIRST"));
            result.add("enum-other-empty=" + invoke(enumConvert, converter, ""));
        }
        Class<?> propertiesType = loader.loadClass("com.sunwayworld.framework.converter.properties.PropertiesToStringConverter");
        Object propertiesConverter = propertiesType.getField("INSTANCE").get(null);
        Method propertiesConvert = propertiesType.getMethod("convert", Properties.class);
        Properties properties = new Properties();
        properties.setProperty("alpha", "one");
        properties.setProperty("unicode", "值");
        result.add("properties-values=" + Arrays.toString((Object[]) propertiesType.getMethod("values").invoke(null)));
        Properties defaults = new Properties(); defaults.setProperty("default", "hidden");
        Properties inherited = new Properties(defaults); inherited.setProperty("special key=", "line1\nline2:!");
        Properties nonString = new Properties(); nonString.put(1, "number-key");
        for (Object input : new Object[] {properties, new Properties(), inherited, nonString, new FailingProperties(), null})
            result.add("properties-convert=" + invoke(propertiesConvert, propertiesConverter, input));
        result.add("properties-source=" + enumSource.invoke(propertiesConverter));
        result.add("properties-target=" + enumTarget.invoke(propertiesConverter));
        result.add("properties-wrong-type=" + invoke(enumConvert, propertiesConverter, "wrong"));
        return result;
    }

    public static void main(String[] args) throws Exception {
        try (URLClassLoader supplier = loader(new File(args[0]), null);
             URLClassLoader owned = loader(null, new File(args[1]))) {
            List<String> expected = observe(supplier);
            List<String> actual = observe(owned);
            if (!expected.equals(actual)) throw new AssertionError("supplier=" + expected + "\nowned=" + actual);
            System.out.println("enum/properties converter differential: PASS (" + expected.size() + " observations)");
        }
    }
}
