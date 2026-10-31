package com.sunwayworld.framework;

import java.io.File;
import java.lang.reflect.*;
import java.math.*;
import java.net.*;
import java.util.*;

/** Dedicated format contract: subclass rejection, correction, and captured locale. */
public final class NumberToStringDifferentialTest {
    private static final Number[] VALUES = {null, (byte) 1, (short) -2, 0, Long.MIN_VALUE, Long.MAX_VALUE,
        new BigInteger("9999999999999999999999999999999999999"), new BigDecimal("1.99999999"),
        new BigDecimal("1.00000000005"), new BigDecimal("-0.00000000001"),
        new BigInteger("19") {}, new BigDecimal("1.25") {},
        new java.util.concurrent.atomic.AtomicInteger(7), new java.util.concurrent.atomic.AtomicLong(-9),
        new Number() { public int intValue(){throw new AssertionError("unexpected intValue");} public long longValue(){throw new AssertionError("unexpected longValue");} public float floatValue(){throw new AssertionError("unexpected floatValue");} public double doubleValue(){throw new AssertionError("unexpected doubleValue");} public String toString(){return "custom";} },
        new Number() { public int intValue(){return 1;} public long longValue(){return 1;} public float floatValue(){return 1;} public double doubleValue(){return 1;} public String toString(){throw new IllegalStateException("toString failed");} },
        1.99999999D, -1.99999999D, 1.099999999D, -1.099999999D, 1.9999999999D, -1.9999999999D,
        0.99999999D, -0.99999999D, 123.999999991D, -123.999999991D, 1.23499999999D,
        1.9999999F, -1.9999999F, 1.99999999F, Float.NaN, Double.NaN, Float.POSITIVE_INFINITY,
        Double.NEGATIVE_INFINITY, -0.0D, -0.0F, Float.MIN_VALUE, Float.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE};
    private static URLClassLoader loader(File deps, File framework) throws Exception {
        List<URL> urls = new ArrayList<URL>(); urls.add(framework.toURI().toURL());
        for (File f : deps.listFiles()) if (f.getName().endsWith(".jar")) urls.add(f.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[0]), null);
    }
    private static List<String> observe(File deps, File framework, Locale initial, Locale changed) throws Exception {
        Locale.setDefault(initial);
        try (URLClassLoader loader = loader(deps, framework)) {
            Class<?> sourceType = loader.loadClass("org.springframework.context.support.StaticMessageSource");
            Object source = sourceType.newInstance();
            sourceType.getMethod("setUseCodeAsDefaultMessage", boolean.class).invoke(source, true);
            Class<?> messageSource = loader.loadClass("org.springframework.context.MessageSource");
            try {
                Class<?> helper = loader.loadClass("com.sunwayworld.framework.i18n.I18nHelper");
                helper.getMethod("setMessageSource", messageSource).invoke(helper.newInstance(), source);
            } catch (ClassNotFoundException owned) {
                loader.loadClass("com.sunwayworld.owned.internal.i18n.OwnedMessageSource").getMethod("setMessageSource", messageSource).invoke(null, source);
            }
            Class<?> converter = loader.loadClass("com.sunwayworld.framework.converter.number.NumberToStringConverter");
            Object instance = converter.getField("INSTANCE").get(null);
            Method convert = converter.getMethod("convert", Number.class);
            convert.invoke(instance, new Object[]{null});
            List<String> out = new ArrayList<String>();
            for (Locale locale : new Locale[]{initial, changed}) {
                Locale.setDefault(locale);
                for (int i = 0; i < VALUES.length; i++) {
                    try { out.add(i + "=" + convert.invoke(instance, VALUES[i])); }
                    catch (InvocationTargetException e) { Throwable error=e.getCause(); out.add(i+"=ERROR:"+error.getClass().getName()+":"+error.getMessage()+":cause="+error.getCause()); }
                }
            }
            return out;
        }
    }
    public static void main(String[] args) throws Exception {
        Locale original = Locale.getDefault(); int count = 0;
        try {
            for (Locale initial : new Locale[]{Locale.US, Locale.GERMANY, Locale.FRANCE, new Locale("ar", "EG")}) {
                Locale changed = initial.equals(Locale.US) ? Locale.GERMANY : Locale.US;
                List<String> expected=observe(new File(args[0]),new File(args[1]),initial,changed);
                List<String> actual=observe(new File(args[2]),new File(args[3]),initial,changed);
                if (!expected.equals(actual)) { for(int i=0;i<expected.size();i++) if(!expected.get(i).equals(actual.get(i))) throw new AssertionError(initial+" observation "+i+" supplier="+expected.get(i)+" owned="+actual.get(i)); }
                count += expected.size();
            }
        } finally { Locale.setDefault(original); }
        System.out.println("number to string differential: PASS ("+count+" observations, 4 initial locales and default mutation)");
    }
}
