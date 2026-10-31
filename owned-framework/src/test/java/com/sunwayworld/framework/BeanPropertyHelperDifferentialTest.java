package com.sunwayworld.framework;

import java.beans.Transient;
import java.io.File;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/** Parentless supplier/owned differential for descriptor lookup and property proxies. */
public final class BeanPropertyHelperDifferentialTest {
    public static class Fixture {
        private String name = "old";
        private int count;
        private String nullable;
        public Fixture() { }
        public String getName() { return name; }
        public void setName(String value) { name = value; }
        public int getCount() { return count; }
        public void setCount(int value) { count = value; }
        public String getNullable() { return nullable; }
        public void setNullable(String value) { nullable = value; }
        @Transient public String getIgnored() { return "ignored"; }
        public void setIgnored(String value) { }
        public static String ignored() { return "ignored"; }
    }
    private static URLClassLoader loader(File deps, File framework, File reference, File fixture) throws Exception {
        List<URL> urls = new ArrayList<URL>(); urls.add(fixture.toURI().toURL()); urls.add(framework.toURI().toURL()); if (reference != null) urls.add(reference.toURI().toURL());
        for (File f : Objects.requireNonNull(deps.listFiles())) if (f.getName().endsWith(".jar")) urls.add(f.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[0]), null);
    }
    private static String listNames(Object value) {
        List<String> names = new ArrayList<String>();
        for (Object descriptor : (List<?>) value) try { names.add(String.valueOf(descriptor.getClass().getMethod("getName").invoke(descriptor))); } catch (Exception e) { throw new AssertionError(e); }
        return names.toString();
    }
    private static void same(String label, Object expected, Object actual) { if (!Objects.equals(String.valueOf(expected), String.valueOf(actual))) throw new AssertionError(label + " supplier=" + expected + " owned=" + actual); }
    private static String proxyObservation(Class<?> helper, Class<?> fixtureType, boolean nullPropertyUndefined) throws Exception {
        Object fixture = fixtureType.newInstance();
        Object proxy = helper.getMethod("getPropertyListenerProxy", Object.class, boolean.class).invoke(null, fixture, nullPropertyUndefined);
        proxy.getClass().getMethod("setName", String.class).invoke(proxy, "new");
        proxy.getClass().getMethod("setIgnored", String.class).invoke(proxy, "ignored");
        proxy.getClass().getMethod("setNullable", String.class).invoke(proxy, new Object[]{null});
        Object events = proxy.getClass().getMethod("getChangedPropertyEventList").invoke(proxy);
        Object event = ((List<?>) events).get(0);
        return proxy.getClass().getMethod("getName").invoke(proxy) + "|" + ((List<?>) events).size() + "|" +
                event.getClass().getMethod("getName").invoke(event) + "|" + event.getClass().getMethod("getOldValue").invoke(event) + "|" + event.getClass().getMethod("getNewValue").invoke(event);
    }
    public static void main(String[] args) throws Exception {
        File deps = new File(args[0]), reference = new File(args[1]), ownedClasses = new File(args[2]), fixture = new File(args[3]);
        try (URLClassLoader supplierLoader = loader(deps, reference, null, fixture); URLClassLoader ownedLoader = loader(deps, ownedClasses, null, fixture)) {
            Class<?> supplierHelper = supplierLoader.loadClass("com.sunwayworld.framework.beans.BeanPropertyHelper");
            Class<?> ownedHelper = ownedLoader.loadClass("com.sunwayworld.framework.beans.BeanPropertyHelper");
            Class<?> supplierFixture = supplierLoader.loadClass("com.sunwayworld.framework.BeanPropertyHelperDifferentialTest$Fixture");
            Class<?> ownedFixture = ownedLoader.loadClass("com.sunwayworld.framework.BeanPropertyHelperDifferentialTest$Fixture");
            same("descriptor list", listNames(supplierHelper.getMethod("getBeanPropertyDescriptorList", Class.class).invoke(null, supplierFixture)), listNames(ownedHelper.getMethod("getBeanPropertyDescriptorList", Class.class).invoke(null, ownedFixture)));
            Object supplierDescriptor = supplierHelper.getMethod("getBeanPropertyDescriptor", Class.class, String.class).invoke(null, supplierFixture, "NAME");
            Object ownedDescriptor = ownedHelper.getMethod("getBeanPropertyDescriptor", Class.class, String.class).invoke(null, ownedFixture, "NAME");
            same("descriptor lookup", supplierDescriptor.getClass().getMethod("getName").invoke(supplierDescriptor), ownedDescriptor.getClass().getMethod("getName").invoke(ownedDescriptor));
            same("valid exact", supplierHelper.getMethod("isValidPropertyName", Class.class, String.class).invoke(null, supplierFixture, "name"), ownedHelper.getMethod("isValidPropertyName", Class.class, String.class).invoke(null, ownedFixture, "name"));
            same("valid case", supplierHelper.getMethod("isValidPropertyName", Class.class, String.class).invoke(null, supplierFixture, "NAME"), ownedHelper.getMethod("isValidPropertyName", Class.class, String.class).invoke(null, ownedFixture, "NAME"));
            same("proxy changed values", proxyObservation(supplierHelper, supplierFixture, false), proxyObservation(ownedHelper, ownedFixture, false));
            same("proxy null assignment", proxyObservation(supplierHelper, supplierFixture, true), proxyObservation(ownedHelper, ownedFixture, true));
            System.out.println("BeanPropertyHelper differential: PASS (descriptor list/lookup, validity, transient filtering, null assignment and change-event proxy)");
        }
    }
}
