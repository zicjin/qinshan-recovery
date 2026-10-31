package com.sunwayworld.framework;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class StringReaderStreamConverterDifferentialTest {
    private static URLClassLoader loader(File jar) throws Exception {
        return new URLClassLoader(new URL[] {jar.toURI().toURL()}, null);
    }

    private static String invoke(Method method, Object receiver, Object... args) throws Exception {
        try {
            Object value = method.invoke(receiver, args);
            if (value == null) return "null";
            if (value instanceof Reader) {
                try (Reader reader = (Reader) value) {
                    StringBuilder content = new StringBuilder();
                    char[] buffer = new char[16]; int count;
                    while ((count = reader.read(buffer)) >= 0) content.append(buffer, 0, count);
                    return value.getClass().getName() + ":" + content;
                }
            }
            if (value instanceof InputStream) {
                try (InputStream stream = (InputStream) value) {
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    byte[] buffer = new byte[16]; int count;
                    while ((count = stream.read(buffer)) >= 0) bytes.write(buffer, 0, count);
                    return value.getClass().getName() + ":" + new String(bytes.toByteArray(), StandardCharsets.UTF_8);
                }
            }
            return value.getClass().getName() + ":" + value;
        } catch (InvocationTargetException failure) {
            Throwable cause = failure.getCause();
            return "!" + cause.getClass().getName() + ":" + String.valueOf(cause.getMessage());
        }
    }

    private static List<String> observe(ClassLoader loader) throws Exception {
        List<String> result = new ArrayList<String>();
        Class<?> converterFactory = loader.loadClass("com.sunwayworld.framework.converter.ConverterFactory");
        Class<?> converter = loader.loadClass("com.sunwayworld.framework.converter.Converter");
        Method getConverter = converterFactory.getMethod("getConverter", Class.class);
        Method convert = converter.getMethod("convert", Object.class);
        Method source = converter.getMethod("getSourceType");
        Method target = converter.getMethod("getTargetType");
        for (String family : new String[] {"Reader", "InputStream"}) {
            Class<?> factoryType = loader.loadClass("com.sunwayworld.framework.converter.string.StringTo" + family + "ConverterFactory");
            Object factory = factoryType.getField("INSTANCE").get(null);
            result.add(family + "-values=" + Arrays.toString((Object[]) factoryType.getMethod("values").invoke(null)));
            Class<?>[] targets = family.equals("Reader")
                    ? new Class<?>[] {java.io.BufferedReader.class, java.io.CharArrayReader.class, java.io.InputStreamReader.class, java.io.LineNumberReader.class, java.io.StringReader.class, java.io.StringWriter.class, null}
                    : new Class<?>[] {java.io.ByteArrayInputStream.class, java.io.BufferedInputStream.class, java.io.DataInputStream.class, java.io.PushbackInputStream.class, java.io.ByteArrayOutputStream.class, null};
            for (Class<?> targetType : targets) {
                Object item = getConverter.invoke(factory, targetType);
                result.add(family + "-source=" + source.invoke(item));
                result.add(family + "-target=" + target.invoke(item));
                for (Object value : new Object[] {null, "", "héllo\n世界",
                        "0123456789abcdef0123456789abcdef世界\r\nsecond line", "\uD83D\uDE00\uD800end", 1})
                    result.add(family + "-convert=" + targetType + ":" + invoke(convert, item, value));
                for (Class<?> from : new Class<?>[] {null, String.class, Object.class})
                    for (Class<?> to : new Class<?>[] {null, targetType, Object.class, String.class})
                        result.add(family + "-can=" + invoke(converter.getMethod("canConvert", Class.class, Class.class), item, from, to));
            }
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        try (URLClassLoader supplier = loader(new File(args[0])); URLClassLoader owned = loader(new File(args[1]))) {
            List<String> expected = observe(supplier), actual = observe(owned);
            if (!expected.equals(actual)) throw new AssertionError("supplier=" + expected + "\nowned=" + actual);
            System.out.println("string reader/stream converter differential: PASS (" + expected.size() + " observations)");
        }
    }
}
