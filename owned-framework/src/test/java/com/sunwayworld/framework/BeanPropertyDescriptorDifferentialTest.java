package com.sunwayworld.framework;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/** Parentless supplier/owned differential for BeanPropertyDescriptor's public seams. */
public final class BeanPropertyDescriptorDifferentialTest {
    public static final class Fixture {
        @Deprecated private int count;
        private String name = "initial";
        public int getCount() { return count; }
        public void setCount(int value) { count = value; }
        @Deprecated public String getName() { return name; }
        public void setName(String value) { name = value; }
        public String getReadOnly() { return "read-only"; }
    }

    private static URLClassLoader loader(File deps, File framework, File reference) throws Exception {
        List<URL> urls = new ArrayList<URL>();
        urls.add(framework.toURI().toURL());
        if (reference != null) urls.add(reference.toURI().toURL());
        for (File f : Objects.requireNonNull(deps.listFiles())) if (f.getName().endsWith(".jar")) urls.add(f.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[0]), null);
    }
    private static String canon(Object value) {
        if (value == null) return "null";
        if (value instanceof Class) return ((Class<?>) value).getName();
        if (value instanceof Method) return ((Method) value).toGenericString();
        if (value instanceof Field) return ((Field) value).toGenericString();
        if (value instanceof Annotation) return value.toString();
        return String.valueOf(value);
    }
    private static String call(Class<?> descriptor, String name, Class<?>[] signature, Object... args) throws Exception {
        try { return canon(descriptor.getMethod(name, signature).invoke(null, args)); }
        catch (InvocationTargetException e) { Throwable cause = e.getCause(); return "ERROR:" + cause.getClass().getName() + ":" + String.valueOf(cause.getMessage()); }
    }
    private static String instance(Class<?> descriptor, String property, String operation, Object fixture, Object... args) throws Exception {
        try {
            Object d = descriptor.getMethod("of", Class.class, String.class).invoke(null, Fixture.class, property);
            if (d == null) return "null";
            if (operation.equals("get")) return canon(d.getClass().getMethod("getPropertyValue", Object.class).invoke(d, fixture));
            if (operation.equals("set")) {
                d.getClass().getMethod("setPropertyValue", Object.class, Object.class).invoke(d, fixture, args[0]);
                return "name=" + ((Fixture) fixture).name + "|count=" + ((Fixture) fixture).count;
            }
            if (operation.equals("annotation")) return canon(d.getClass().getMethod("getAnnotationIfPresent", Class.class).invoke(d, Deprecated.class));
            if (operation.equals("meta")) return canon(d.getClass().getMethod("getName").invoke(d)) + "|" + canon(d.getClass().getMethod("getPropertyType").invoke(d)) + "|" + canon(d.getClass().getMethod("isValidBeanProperty").invoke(d)) + "|" + canon(d.getClass().getMethod("isExt$BeanProperty").invoke(d));
            return "unknown";
        } catch (InvocationTargetException e) { Throwable cause = e.getCause(); return "ERROR:" + cause.getClass().getName() + ":" + String.valueOf(cause.getMessage()); }
    }
    private static void same(String label, String expected, String actual) { if (!Objects.equals(expected, actual)) throw new AssertionError(label + " supplier=" + expected + " owned=" + actual); }

    public static void main(String[] args) throws Exception {
        File deps = new File(args[0]), reference = new File(args[1]), ownedClasses = new File(args[2]);
        try (URLClassLoader supplierLoader = loader(deps, reference, null); URLClassLoader ownedLoader = loader(deps, ownedClasses, null)) {
            Class<?> supplier = supplierLoader.loadClass("com.sunwayworld.framework.beans.BeanPropertyDescriptor");
            Class<?> owned = ownedLoader.loadClass("com.sunwayworld.framework.beans.BeanPropertyDescriptor");
            for (String property : new String[]{"name", "count", "missing", "readOnly"}) {
                same("of " + property, call(supplier, "of", new Class<?>[]{Class.class, String.class}, Fixture.class, property), call(owned, "of", new Class<?>[]{Class.class, String.class}, Fixture.class, property));
                same("meta " + property, instance(supplier, property, "meta", new Fixture()), instance(owned, property, "meta", new Fixture()));
            }
            Object supplierName = supplier.getMethod("of", Class.class, String.class).invoke(null, Fixture.class, "name");
            Object ownedName = owned.getMethod("of", Class.class, String.class).invoke(null, Fixture.class, "name");
            Object supplierNameAgain = supplier.getMethod("of", Class.class, String.class).invoke(null, Fixture.class, "name");
            Object ownedNameAgain = owned.getMethod("of", Class.class, String.class).invoke(null, Fixture.class, "name");
            same("cache identity", String.valueOf(supplierName == supplierNameAgain), String.valueOf(ownedName == ownedNameAgain));
            same("field factory", call(supplier, "of", new Class<?>[]{Field.class}, Fixture.class.getDeclaredField("name")), call(owned, "of", new Class<?>[]{Field.class}, Fixture.class.getDeclaredField("name")));
            Fixture supplierFixture = new Fixture(), ownedFixture = new Fixture();
            same("get name", instance(supplier, "name", "get", supplierFixture), instance(owned, "name", "get", ownedFixture));
            same("set name", instance(supplier, "name", "set", supplierFixture, 17), instance(owned, "name", "set", ownedFixture, 17));
            same("set count conversion", instance(supplier, "count", "set", supplierFixture, "17"), instance(owned, "count", "set", ownedFixture, "17"));
            same("annotation", instance(supplier, "name", "annotation", supplierFixture), instance(owned, "name", "annotation", ownedFixture));
            Fixture supplierSource = new Fixture(), supplierTarget = new Fixture(), ownedSource = new Fixture(), ownedTarget = new Fixture();
            supplierSource.name = "copied"; ownedSource.name = "copied";
            supplier.getMethod("of", Class.class, String.class).invoke(null, Fixture.class, "name").getClass().getMethod("passValue", Object.class, Object.class).invoke(supplierName, supplierSource, supplierTarget);
            owned.getMethod("of", Class.class, String.class).invoke(null, Fixture.class, "name").getClass().getMethod("passValue", Object.class, Object.class).invoke(ownedName, ownedSource, ownedTarget);
            same("pass value", supplierTarget.name + "|" + supplierTarget.count, ownedTarget.name + "|" + ownedTarget.count);
            same("equals", String.valueOf(supplierName.equals(supplierNameAgain)), String.valueOf(ownedName.equals(ownedNameAgain)));
            System.out.println("BeanPropertyDescriptor differential: PASS (public metadata, cache, read/write, conversion, annotation, errors)");
        }
    }
}
