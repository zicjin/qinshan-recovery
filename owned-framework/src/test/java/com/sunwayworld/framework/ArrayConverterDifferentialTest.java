package com.sunwayworld.framework;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Invokes erased conversion methods in independent supplier and owned loaders. */
public final class ArrayConverterDifferentialTest {
    private static final String PREFIX = "com.sunwayworld.framework.converter.array.";
    private static final String[] TYPES = {"Boolean", "Byte", "Character", "Double", "Float", "Integer", "Long", "Short"};
    private static final Class<?>[] PRIMITIVES = {boolean.class, byte.class, char.class, double.class, float.class, int.class, long.class, short.class};
    private static final Class<?>[] WRAPPERS = {Boolean.class, Byte.class, Character.class, Double.class, Float.class, Integer.class, Long.class, Short.class};
    private static final Object[][] VALUES = {{true, false}, {(byte)-128, (byte)127}, {'\0', '中'}, {Double.NaN, -0.0d, Double.POSITIVE_INFINITY}, {Float.NaN, -0.0f, Float.NEGATIVE_INFINITY}, {Integer.MIN_VALUE, Integer.MAX_VALUE}, {Long.MIN_VALUE, Long.MAX_VALUE}, {Short.MIN_VALUE, Short.MAX_VALUE}};
    private static String value(Object object) {
        if (object == null) return "null";
        if (!object.getClass().isArray()) return object.toString();
        List<String> values = new ArrayList<String>();
        for (int i = 0; i < Array.getLength(object); i++) values.add(String.valueOf(Array.get(object, i)));
        return object.getClass().getName() + values;
    }
    private static String invoke(Method method, Object instance, Object... args) throws Exception {
        try { return value(method.invoke(instance, args)); }
        catch (InvocationTargetException e) { return "!" + e.getCause().getClass().getName(); }
    }
    private static List<String> observe(ClassLoader loader) throws Exception {
        List<String> observations = new ArrayList<String>();
        for (String direction : new String[]{"PrimitiveToWrapper", "WrapperToPrimitive"}) {
            String family = direction + "ArrayConverter";
            for (int i = 0; i < TYPES.length; i++) {
                Class<?> converter = loader.loadClass(PREFIX + family + "$" + direction + TYPES[i] + "ArrayConverter");
                Object singleton = converter.getField("INSTANCE").get(null);
                Method convert = converter.getMethod("convert", Object.class);
                observations.add(converter.getName() + ":source=" + converter.getMethod("getSourceType").invoke(singleton));
                observations.add(converter.getName() + ":target=" + converter.getMethod("getTargetType").invoke(singleton));
                boolean box = direction.equals("PrimitiveToWrapper");
                Class<?> component = box ? PRIMITIVES[i] : WRAPPERS[i];
                Object populated = Array.newInstance(component, VALUES[i].length);
                for (int n = 0; n < VALUES[i].length; n++) Array.set(populated, n, VALUES[i][n]);
                Object wrong = box ? new int[]{7} : new Integer[]{7, null};
                for (Object input : new Object[]{null, Array.newInstance(component, 0), populated, new Object[]{1}, wrong, "wrong", box ? Array.newInstance(component, 0) : Array.newInstance(component, 1)}) {
                    observations.add(converter.getName() + ":convert=" + invoke(convert, singleton, input));
                }
                Class<?> source = Array.newInstance(component, 0).getClass();
                Class<?> target = Array.newInstance(box ? WRAPPERS[i] : PRIMITIVES[i], 0).getClass();
                Method canConvert = converter.getMethod("canConvert", Class.class, Class.class);
                observations.add(converter.getName() + ":accept=" + invoke(canConvert, singleton, source, target));
                observations.add(converter.getName() + ":reject=" + invoke(canConvert, singleton, String.class, Object.class));
                Object result = convert.invoke(singleton, populated);
                observations.add(converter.getName() + ":fresh=" + (result != populated));
                Array.set(result, 0, VALUES[i][VALUES[i].length - 1]);
                observations.add(converter.getName() + ":input=" + value(populated));
                Object[] enums = (Object[])converter.getMethod("values").invoke(null);
                enums[0] = null;
                observations.add(converter.getName() + ":enum=" + (converter.getMethod("valueOf", String.class).invoke(null, "INSTANCE") == singleton)
                        + ":valuesCopy=" + (((Object[])converter.getMethod("values").invoke(null))[0] == singleton));
            }
        }
        return observations;
    }
    public static void main(String[] args) throws Exception {
        java.util.List<URL> supplierUrls = new java.util.ArrayList<URL>();
        for (File dependency : new File(args[0]).listFiles()) supplierUrls.add(dependency.toURI().toURL());
        try (URLClassLoader supplier = new URLClassLoader(supplierUrls.toArray(new URL[0]), null);
             URLClassLoader owned = new URLClassLoader(new URL[]{new File(args[1]).toURI().toURL()}, null)) {
            List<String> expected = observe(supplier), actual = observe(owned);
            if (expected.size() != actual.size()) throw new AssertionError("observation count");
            for (int i = 0; i < expected.size(); i++) if (!expected.get(i).equals(actual.get(i))) throw new AssertionError("supplier=" + expected.get(i) + "\nowned=" + actual.get(i));
            for (String observation : expected) System.out.println(observation);
            System.out.println("array converter differential: PASS (" + expected.size() + " observations)");
        }
    }
}
