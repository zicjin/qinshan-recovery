package com.sunwayworld.framework;

import java.io.*;
import java.lang.reflect.*;
import java.math.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.lang.reflect.Array;
import java.time.*;
import java.util.*;
import javax.sql.rowset.serial.*;

/** Registry observations use fresh input per operation because canConvert performs conversion. */
public final class ConvertUtilsDifferentialTest {
    public enum Choice { FIRST, SECOND }
    private static int checks;
    private static URLClassLoader loader(File dependencies, File framework, File fixtures) throws Exception {
        List<URL> urls = new ArrayList<URL>();
        urls.add(framework.toURI().toURL());
        urls.add(fixtures.toURI().toURL());
        for (File file : dependencies.listFiles()) if (file.getName().endsWith(".jar")) urls.add(file.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[0]), null);
    }
    private static String failure(Throwable error) {
        return error.getClass().getName() + ":" + String.valueOf(error.getMessage())
                + (error.getCause() == null ? "" : " <- " + failure(error.getCause()));
    }
    private static String describe(Object value) throws Exception {
        if (value == null) return "null";
        Class<?> type = value.getClass();
        if (type == Object.class) return type.getName();
        if (type.isArray()) {
            List<String> items = new ArrayList<String>();
            for (int i = 0; i < Array.getLength(value); i++) items.add(describe(Array.get(value, i)));
            return type.getName() + items;
        }
        if (value instanceof Blob) {
            Blob blob = (Blob) value;
            long length = blob.length();
            return type.getName() + Arrays.toString(length == 0 ? new byte[0] : blob.getBytes(1, (int) length));
        }
        if (value instanceof Clob) {
            Clob clob = (Clob) value;
            long length = clob.length();
            return type.getName() + ":" + (length == 0 ? "" : clob.getSubString(1, (int) length));
        }
        if (value instanceof Reader) {
            try (Reader reader = (Reader) value) {
                StringBuilder out = new StringBuilder(); char[] buffer = new char[17]; int n;
                while ((n = reader.read(buffer)) >= 0) out.append(buffer, 0, n);
                return type.getName() + ":" + out;
            }
        }
        if (value instanceof InputStream) {
            try (InputStream stream = (InputStream) value) {
                ByteArrayOutputStream out = new ByteArrayOutputStream(); int b;
                while ((b = stream.read()) != -1) out.write(b);
                return type.getName() + Arrays.toString(out.toByteArray());
            }
        }
        if (value instanceof java.util.Date) return type.getName() + ":millis=" + ((java.util.Date) value).getTime();
        if (type.getName().equals("oracle.sql.DATE") || type.getName().equals("oracle.sql.TIMESTAMP"))
            return type.getName() + Arrays.toString((byte[]) type.getMethod("getBytes").invoke(value));
        String text = String.valueOf(value);
        if (value instanceof String && text.startsWith("#") && text.indexOf('\n') >= 0)
            text = "#TIMESTAMP\n" + text.substring(text.indexOf('\n') + 1);
        return type.getName() + ":" + text;
    }
    private static Object input(int index, ClassLoader loader) throws Exception {
        switch (index) {
            case 0: return null;
            case 1: return "";
            case 2: return "17";
            case 3: return " TRUE ";
            case 4: return "FIRST";
            case 5: return "2026-10-19 12:34:56.123456";
            case 6: return 17;
            case 7: return -2.5d;
            case 8: return new BigDecimal("123456789012345.123456789012");
            case 9: return new BigInteger("12345678901234567890123456789");
            case 10: return true;
            case 11: return new java.util.Date(123456789L);
            case 12: return new java.sql.Time(123456789L);
            case 13: return LocalDate.of(2026, 10, 19);
            case 14: return LocalDateTime.of(2026, 10, 19, 12, 34, 56, 123456789);
            case 15: return LocalTime.of(12, 34, 56, 123456789);
            case 16: return new byte[] {65, -1, 66};
            case 17: return new Integer[] {1, null, 3};
            case 18: return new int[] {1, 2, 3};
            case 19: return new char[] {'a', '中'};
            case 20: return new ArrayList<Object>(Arrays.<Object>asList("x", 1, null));
            case 21: { Map<String,Object> map = new LinkedHashMap<String,Object>(); map.put("x", 1); map.put("nested", Arrays.asList(2, 3)); return map; }
            case 22: return new StringReader("alpha\r\nbeta");
            case 23: return new SerialBlob("héllo世界".getBytes(StandardCharsets.UTF_8));
            case 24: return new SerialClob("héllo世界".toCharArray());
            case 25: { Properties p = new Properties(); p.setProperty("key", "value"); return p; }
            case 26: return loader.loadClass("com.alibaba.fastjson.JSONObject").getMethod("parseObject", String.class).invoke(null, "{\"x\":1}");
            case 27: return new Object();
            case 28: return new Byte[] {1, 2};
            case 29: return new java.sql.Timestamp(123456789L);
            case 30: return new boolean[] {true, false};
            case 31: return new Boolean[] {true, null, false};
            case 32: return new short[] {1, -2};
            case 33: return new Short[] {1, null, -2};
            case 34: return new long[] {1L, Long.MIN_VALUE};
            case 35: return new Long[] {1L, null, Long.MIN_VALUE};
            case 36: return new float[] {1.25F, Float.NaN};
            case 37: return new Float[] {1.25F, null, Float.NaN};
            case 38: return new double[] {-2.5D, Double.POSITIVE_INFINITY};
            case 39: return new Double[] {-2.5D, null, Double.POSITIVE_INFINITY};
            case 40: return new Character[] {'a', null, '中'};
            case 41: return new SerialBlob(new byte[0]);
            case 42: return new SerialClob(new char[0]);
            case 43: return loader.loadClass("oracle.sql.DATE").getConstructor(java.sql.Timestamp.class)
                    .newInstance(java.sql.Timestamp.valueOf("2024-02-29 23:45:12"));
            case 44: return loader.loadClass("oracle.sql.TIMESTAMP").getConstructor(java.sql.Timestamp.class)
                    .newInstance(java.sql.Timestamp.valueOf("2024-02-29 23:45:12.987654321"));
            case 45: return new java.sql.Date(123456789L);
            case 46: return loader.loadClass(Choice.class.getName()).getEnumConstants()[0];
            case 47: return new ByteArrayInputStream(new byte[] {65, 66});
            default: throw new AssertionError(index);
        }
    }
    private static List<String> observe(ClassLoader loader) throws Exception {
        Class<?> utility = loader.loadClass("com.sunwayworld.framework.utils.ConvertUtils");
        Method convert = utility.getMethod("convert", Object.class, Class.class);
        Method withDefault = utility.getMethod("convert", Object.class, Class.class, Object.class);
        Method canConvert = utility.getMethod("canConvert", Object.class, Class.class);
        Class<?>[] targets = {null, Object.class, String.class, boolean.class, Boolean.class, byte.class, Byte.class,
                char.class, Character.class, short.class, Short.class, Integer.class, int.class, Long.class, long.class, Float.class, float.class, Double.class,
                double.class, Number.class, BigDecimal.class, BigInteger.class, java.util.Date.class, java.sql.Date.class,
                java.sql.Time.class, java.sql.Timestamp.class, LocalDate.class, LocalDateTime.class, LocalTime.class,
                java.time.temporal.Temporal.class, Map.class, Iterable.class, Properties.class, Reader.class, StringReader.class,
                InputStream.class, ByteArrayInputStream.class, Blob.class, Clob.class, byte[].class, Byte[].class,
                int[].class, Integer[].class, char[].class, Character[].class, loader.loadClass(Choice.class.getName()),
                loader.loadClass("com.alibaba.fastjson.JSONObject"), boolean[].class, Boolean[].class,
                short[].class, Short[].class, long[].class, Long[].class, float[].class, Float[].class,
                double[].class, Double[].class, void.class, Void.class,
                loader.loadClass("oracle.sql.DATE"), loader.loadClass("oracle.sql.TIMESTAMP")};
        List<String> observations = new ArrayList<String>();
        for (int i = 0; i < 48; i++) for (Class<?> target : targets) {
            for (Method operation : new Method[] {convert, withDefault, canConvert}) {
                Object source = input(i, loader);
                String prefix = "input#" + i + " target=" + target + " " + operation.getName() + "/" + operation.getParameterCount();
                try {
                    Object value = operation.getParameterCount() == 3 ? operation.invoke(null, source, target, "DEFAULT") : operation.invoke(null, source, target);
                    observations.add(prefix + "=" + describe(value) + " identity=" + (source != null && source == value));
                } catch (InvocationTargetException error) { observations.add(prefix + "=!" + failure(error.getCause())); }
                finally { if (source instanceof Closeable) ((Closeable) source).close(); }
            }
        }
        return observations;
    }
    public static void main(String[] args) throws Exception {
        try (URLClassLoader reference = loader(new File(args[0]), new File(args[1]), new File(args[4]));
             URLClassLoader owned = loader(new File(args[2]), new File(args[3]), new File(args[4]))) {
            List<String> expected = observe(reference), actual = observe(owned);
            if (expected.size() != actual.size()) throw new AssertionError("observation count differs");
            for (int i = 0; i < expected.size(); i++) {
                checks++;
                if (!expected.get(i).equals(actual.get(i))) throw new AssertionError("supplier=" + expected.get(i) + "\nowned=" + actual.get(i));
            }
            System.out.println("ConvertUtils differential: PASS (" + checks + " observations)");
        }
    }
}
