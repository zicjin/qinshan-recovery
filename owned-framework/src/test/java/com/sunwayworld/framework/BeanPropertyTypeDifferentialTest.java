package com.sunwayworld.framework;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Differential observations for the closed BeanPropertyType contract. */
public final class BeanPropertyTypeDifferentialTest {
    private static int observations;

    private static URLClassLoader loader(File deps, File framework) throws Exception {
        List<URL> urls = new ArrayList<URL>();
        urls.add(framework.toURI().toURL());
        for (File file : deps.listFiles()) if (file.getName().endsWith(".jar")) urls.add(file.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[0]), null);
    }

    private static String lookup(Class<?> type, String name) throws Exception {
        try {
            return ((Enum<?>) type.getMethod("valueOf", String.class).invoke(null, name)).name();
        } catch (InvocationTargetException failure) {
            return "ERROR:" + failure.getCause().getClass().getName() + ":" + failure.getCause().getMessage();
        }
    }

    private static void same(Object expected, Object actual, String label) {
        observations++;
        if (!java.util.Objects.equals(expected, actual)) throw new AssertionError(label + " supplier=" + expected + " owned=" + actual);
    }

    private static String observe(Class<?> type, Class<?> input) throws Exception {
        try {
            Method method = type.getMethod("of", Class.class);
            Object result = method.invoke(null, input);
            return result == null ? "null" : ((Enum<?>) result).name();
        } catch (InvocationTargetException failure) {
            Throwable cause = failure.getCause();
            return "ERROR:" + cause.getClass().getName();
        }
    }

    public static void main(String[] args) throws Exception {
        File deps = new File(args[0]);
        File reference = new File(args[1]);
        File owned = new File(args[2]);
        List<Class<?>> inputs = Arrays.<Class<?>>asList(
                null, String.class, CharSequence.class, Double.class, Double.TYPE,
                Float.class, Float.TYPE, Long.class, Long.TYPE, Integer.class, Integer.TYPE,
                Short.class, Short.TYPE, Byte.class, Byte.TYPE, Boolean.class, Boolean.TYPE,
                LocalDate.class, LocalDateTime.class, java.util.Date.class, Object.class,
                java.math.BigDecimal.class, String[].class);
        try (URLClassLoader supplier = loader(deps, reference); URLClassLoader own = loader(deps, owned)) {
            Class<?> supplierType = supplier.loadClass("com.sunwayworld.framework.beans.BeanPropertyType");
            Class<?> ownedType = own.loadClass("com.sunwayworld.framework.beans.BeanPropertyType");
            if (!ownedType.getProtectionDomain().getCodeSource().getLocation().equals(owned.toURI().toURL())) throw new AssertionError("owned enum provenance");
            for (int pass = 0; pass < 2; pass++) for (Class<?> input : inputs) {
                String expected = observe(supplierType, input);
                String actual = observe(ownedType, input);
                same(expected, actual, "mapping " + input + " pass " + pass);
            }
            Object supplierValues = supplierType.getMethod("values").invoke(null);
            Object ownedValues = ownedType.getMethod("values").invoke(null);
            same(java.lang.reflect.Array.getLength(supplierValues), java.lang.reflect.Array.getLength(ownedValues), "enum size");
            for (int i = 0; i < java.lang.reflect.Array.getLength(supplierValues); i++) {
                String a = ((Enum<?>) java.lang.reflect.Array.get(supplierValues, i)).name();
                String b = ((Enum<?>) java.lang.reflect.Array.get(ownedValues, i)).name();
                same(a, b, "enum order");
                same(lookup(supplierType, a), lookup(ownedType, a), "enum valueOf");
            }
            for (String name : new String[]{null, "", "string", " STRING", "INVALID"}) same(lookup(supplierType, name), lookup(ownedType, name), "invalid enum lookup " + name);
            java.lang.reflect.Array.set(supplierValues, 0, null);
            java.lang.reflect.Array.set(ownedValues, 0, null);
            same(java.lang.reflect.Array.get(supplierType.getMethod("values").invoke(null), 0) != null, java.lang.reflect.Array.get(ownedType.getMethod("values").invoke(null), 0) != null, "values array isolation");
            System.out.println("BeanPropertyType differential: PASS (" + observations + " observations; isolated supplier-free owned loading)");
        }
    }
}
