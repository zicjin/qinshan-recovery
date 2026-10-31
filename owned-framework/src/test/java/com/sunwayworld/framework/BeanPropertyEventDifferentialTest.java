package com.sunwayworld.framework;

import java.io.File;
import java.io.ObjectStreamClass;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/** Parentless supplier/owned differential for the BeanPropertyEvent data seam. */
public final class BeanPropertyEventDifferentialTest {
    public static final class Fixture { private String name; public String getName() { return name; } public void setName(String v) { name = v; } }
    private static URLClassLoader loader(File deps, File framework, File reference) throws Exception {
        List<URL> urls = new ArrayList<URL>(); urls.add(framework.toURI().toURL()); if (reference != null) urls.add(reference.toURI().toURL());
        for (File f : Objects.requireNonNull(deps.listFiles())) if (f.getName().endsWith(".jar")) urls.add(f.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[0]), null);
    }
    private static void same(String label, Object a, Object b) { if (!Objects.equals(String.valueOf(a), String.valueOf(b))) throw new AssertionError(label + " supplier=" + a + " owned=" + b); }
    public static void main(String[] args) throws Exception {
        try (URLClassLoader supplierLoader = loader(new File(args[0]), new File(args[1]), null); URLClassLoader ownedLoader = loader(new File(args[0]), new File(args[2]), null)) {
            Class<?> sd = supplierLoader.loadClass("com.sunwayworld.framework.beans.BeanPropertyDescriptor");
            Class<?> od = ownedLoader.loadClass("com.sunwayworld.framework.beans.BeanPropertyDescriptor");
            Object supplierDescriptor = sd.getMethod("of", Class.class, String.class).invoke(null, Fixture.class, "name");
            Object ownedDescriptor = od.getMethod("of", Class.class, String.class).invoke(null, Fixture.class, "name");
            Class<?> se = supplierLoader.loadClass("com.sunwayworld.framework.beans.BeanPropertyEvent");
            Class<?> oe = ownedLoader.loadClass("com.sunwayworld.framework.beans.BeanPropertyEvent");
            Object supplier = se.getConstructor(Object.class, sd).newInstance("source", supplierDescriptor);
            Object owned = oe.getConstructor(Object.class, od).newInstance("source", ownedDescriptor);
            same("source", se.getMethod("getSource").invoke(supplier), oe.getMethod("getSource").invoke(owned));
            same("serial UID", ObjectStreamClass.lookup(se).getSerialVersionUID(), ObjectStreamClass.lookup(oe).getSerialVersionUID());
            for (String property : new String[]{"getName", "getOldValue", "getNewValue", "getPropertyDescriptor"}) {
                String a = String.valueOf(se.getMethod(property).invoke(supplier));
                String b = String.valueOf(oe.getMethod(property).invoke(owned));
                if (property.equals("getPropertyDescriptor")) { a = String.valueOf(a.contains("BeanPropertyDescriptor")); b = String.valueOf(b.contains("BeanPropertyDescriptor")); }
                same(property, a, b);
            }
            se.getMethod("setName", String.class).invoke(supplier, "name"); oe.getMethod("setName", String.class).invoke(owned, "name");
            se.getMethod("setOldValue", Object.class).invoke(supplier, 1); oe.getMethod("setOldValue", Object.class).invoke(owned, 1);
            se.getMethod("setNewValue", Object.class).invoke(supplier, "new"); oe.getMethod("setNewValue", Object.class).invoke(owned, "new");
            for (String property : new String[]{"getName", "getOldValue", "getNewValue"}) same(property, se.getMethod(property).invoke(supplier), oe.getMethod(property).invoke(owned));
            System.out.println("BeanPropertyEvent differential: PASS (constructor, source, descriptor, values, serial UID)");
        }
    }
}
