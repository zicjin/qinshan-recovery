package com.sunwayworld.framework;

import java.io.File;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

/** Verifies all ApplicationRuntimeException constructors through real Spring StaticMessageSource. */
public final class ApplicationRuntimeExceptionDifferentialTest {
    private static URLClassLoader loader(File deps, File framework) throws Exception {
        List<URL> urls = new ArrayList<URL>(); urls.add(framework.toURI().toURL());
        for (File file : deps.listFiles()) if (file.getName().endsWith(".jar")) urls.add(file.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[0]), null);
    }
    private static Object configure(ClassLoader loader) throws Exception {
        Class<?> sourceType = loader.loadClass("org.springframework.context.support.StaticMessageSource");
        Object source = sourceType.newInstance();
        Method add = sourceType.getMethod("addMessage", String.class, Locale.class, String.class);
        add.invoke(source, "translated", Locale.US, "Translated {0}");
        add.invoke(source, "fallback", Locale.US, "fallback");
        add.invoke(source, "fallback", Locale.CHINA, "Fallback {0}");
        for (String key : new String[]{"blank-result", "unicode-result"}) {
            add.invoke(source, key, Locale.US, key.equals("blank-result") ? "" : "\u2003");
            add.invoke(source, key, Locale.CHINA, "China {0}");
        }
        add.invoke(source, "translated", Locale.CHINA, "Translated China {0}");
        add.invoke(source, "literal", Locale.US, "literal");
        add.invoke(source, "literal", Locale.CHINA, "literal");
        Class<?> localeHolder = loader.loadClass("org.springframework.context.i18n.LocaleContextHolder");
        localeHolder.getMethod("setLocale", Locale.class).invoke(null, Locale.US);
        try {
            Class<?> helper = loader.loadClass("com.sunwayworld.framework.i18n.I18nHelper");
            Object instance = helper.newInstance();
            helper.getMethod("setMessageSource", loader.loadClass("org.springframework.context.MessageSource")).invoke(instance, source);
        } catch (ClassNotFoundException missingSupplierFacade) {
            loader.loadClass("com.sunwayworld.owned.internal.i18n.OwnedMessageSource").getMethod("setMessageSource", loader.loadClass("org.springframework.context.MessageSource")).invoke(null, source);
        }
        return source;
    }
    private static String result(Constructor<?> constructor, Object... args) throws Exception {
        try {
            Throwable value = (Throwable) constructor.newInstance(args);
            Throwable expectedCause = args[0] instanceof Throwable ? (Throwable) args[0] : null;
            return "MESSAGE:" + value.getMessage() + "|causeIdentity=" + (value.getCause() == expectedCause);
        } catch (InvocationTargetException e) {
            Throwable error = e.getCause();
            return "ERROR:" + error.getClass().getName() + ":" + error.getMessage();
        }
    }
    private static List<String> observe(ClassLoader loader) throws Exception {
        configure(loader);
        Class<?> exception = loader.loadClass("com.sunwayworld.framework.exception.core.ApplicationRuntimeException");
        Constructor<?> message = exception.getConstructor(String.class, String[].class);
        Constructor<?> cause = exception.getConstructor(Throwable.class);
        Constructor<?> causeMessage = exception.getConstructor(Throwable.class, String.class, String[].class);
        List<String> result = new ArrayList<String>();
        result.add(((Throwable) message.newInstance("translated", new String[] {"value"})).getMessage());
        result.add(((Throwable) message.newInstance("fallback", new String[] {"value"})).getMessage());
        result.add(((Throwable) message.newInstance("", new String[0])).getMessage());
        Throwable root = new IllegalStateException("root");
        Throwable wrapped = (Throwable) causeMessage.newInstance(root, "translated", new String[] {"cause"});
        result.add(wrapped.getMessage() + ":" + wrapped.getCause().getClass().getName() + ":" + wrapped.getCause().getMessage());
        result.add(((Throwable) cause.newInstance(root)).getMessage());
        for (Locale locale : new Locale[]{Locale.US, Locale.CHINA, Locale.UK, Locale.GERMANY}) {
            loader.loadClass("org.springframework.context.i18n.LocaleContextHolder").getMethod("setLocale", Locale.class).invoke(null, locale);
            for (String key : new String[]{null, "", " ", "\u2003", "\u3000", "\u00a0", "\u0000", "translated", "fallback", "blank-result", "unicode-result", "literal", "missing"}) {
                for (String[] values : new String[][]{null, new String[0], new String[]{"value"}, new String[]{null}}) {
                    result.add(result(message, key, values));
                    for (Throwable nested : new Throwable[]{null, root, new IllegalArgumentException("nested", root)}) result.add(result(causeMessage, nested, key, values));
                }
            }
        }
        for (Throwable nested : new Throwable[]{null, root, new IllegalArgumentException("nested", root), new RuntimeException() { public String toString() { throw new IllegalStateException("cause text failed"); } }}) result.add(result(cause, nested));
        return result;
    }
    public static void main(String[] args) throws Exception {
        try (URLClassLoader supplier = loader(new File(args[0]), new File(args[1])); URLClassLoader owned = loader(new File(args[2]), new File(args[3]))) {
            List<String> expected = observe(supplier), actual = observe(owned);
            for (int i=0;i<expected.size();i++) if (!expected.get(i).equals(actual.get(i))) throw new AssertionError("observation "+i+" supplier=" + expected.get(i) + " owned=" + actual.get(i));
            System.out.println("application runtime exception differential: PASS ("+expected.size()+" observations, StaticMessageSource, all constructors)");
        }
    }
}
