package com.sunwayworld.framework;

import java.io.File;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.io.*;

/** Parentless differential probe for every public map operation and constructor. */
public final class CaseInsensitiveLinkedMapDifferentialTest {
    private static Object deserialize(byte[] bytes, final ClassLoader loader) throws Exception {
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes)) {
            @Override protected Class<?> resolveClass(ObjectStreamClass descriptor) throws IOException, ClassNotFoundException {
                try { return Class.forName(descriptor.getName(), false, loader); }
                catch (ClassNotFoundException missing) { return super.resolveClass(descriptor); }
            }
        }) { return input.readObject(); }
    }
    private static byte[] serialize(Object value) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) { output.writeObject(value); }
        return bytes.toByteArray();
    }
    private static String failure(Throwable error) {
        if (error instanceof InvocationTargetException) error = ((InvocationTargetException)error).getCause();
        return error.getClass().getName() + (error.getCause() == null ? "" : ":cause=" + error.getCause().getClass().getName());
    }
    private static Set<String> publicMethods(Class<?> type) {
        Set<String> result = new TreeSet<String>();
        for (Method method : type.getDeclaredMethods()) if (Modifier.isPublic(method.getModifiers())) {
            result.add(method.getName() + Arrays.toString(method.getParameterTypes()) + method.getReturnType().getName());
        }
        return result;
    }
    private static URLClassLoader loader(File deps, File framework) throws Exception {
        List<URL> urls = new ArrayList<URL>();
        urls.add(framework.toURI().toURL());
        for (File f : deps.listFiles()) if (f.getName().endsWith(".jar")) urls.add(f.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), null);
    }
    private static List<String> observe(ClassLoader l) throws Exception {
        Class<?> type = l.loadClass("com.sunwayworld.framework.data.CaseInsensitiveLinkedMap");
        Map<String,Object> map = (Map<String,Object>) type.newInstance();
        List<String> out = new ArrayList<String>();
        map.put("UPPER", "one"); map.putIfAbsent("Upper", "two");
        out.add("initial=" + map + ":" + map.get("uPpEr") + ":" + map.containsKey("UPPER"));
        out.add("default=" + map.getOrDefault("uPPER", "missing"));
        map.replace("UPPER", "one", "three"); map.replace("UPPER", "four");
        map.computeIfAbsent("ABSENT", key -> key); map.computeIfPresent("UPPER", (key,value) -> key + value);
        map.compute("COMPUTE", (key,value) -> key); map.merge("UPPER", "plus", (a,b) -> String.valueOf(a)+b);
        out.add("mutators=" + map);
        out.add("removePair=" + map.remove("UPPER", "upperfourplus"));
        out.add("remove=" + map.remove("ABSENT"));
        map.put(null, "null"); out.add("null=" + map.get(null) + ":" + map.get("null"));
        Map<String,String> source = new LinkedHashMap<String,String>(); source.put("First", "1"); source.put("Second", "2");
        Map<String,String> copy = (Map<String,String>) type.getConstructor(Map.class).newInstance(source);
        source.put("Third", "3"); out.add("copy=" + copy + ":source=" + source);
        map.putAll(source); out.add("putAll=" + map);
        Map<String,Object> nonSerializable = new LinkedHashMap<String,Object>();
        nonSerializable.put("bad", new Object());
        for (String op : new String[]{"constructor", "putAll"}) {
            try {
                if (op.equals("constructor")) type.getConstructor(Map.class).newInstance(nonSerializable);
                else map.putAll(nonSerializable);
                out.add(op + "NonSerializable=accepted");
            } catch (Throwable e) { Throwable t=e instanceof InvocationTargetException ? ((InvocationTargetException)e).getCause() : e; out.add(op + "NonSerializable=" + t.getClass().getName()); }
        }
        for (Constructor<?> constructor : type.getConstructors()) {
            Class<?>[] signature = constructor.getParameterTypes();
            if (signature.length == 1 && signature[0] == int.class) {
                out.add("capacity="+constructor.newInstance(0));
                try { constructor.newInstance(-1); out.add("negative-capacity=accepted"); } catch (Throwable error) { out.add("negative-capacity="+failure(error)); }
            }
        }
        for (String op : new String[]{"constructor", "putAll"}) {
            try {
                if (op.equals("constructor")) type.getConstructor(Map.class).newInstance(new Object[]{null});
                else map.putAll(null);
                out.add(op + "Null=accepted");
            } catch (Throwable e) { Throwable t=e instanceof InvocationTargetException ? ((InvocationTargetException)e).getCause() : e; out.add(op + "Null=" + t.getClass().getName()); }
        }
        Map<String,String> ordered = (Map<String,String>) type.getConstructor(int.class,float.class,boolean.class).newInstance(16,0.75f,true);
        ordered.put("A", "1"); ordered.put("B", "2"); ordered.get("A"); out.add("order=" + ordered.keySet());
        Map<String,String> callbacks = (Map<String,String>) type.newInstance();
        callbacks.put("callback", null);
        callbacks.computeIfAbsent("CALLBACK", key -> key.toUpperCase(Locale.ROOT));
        callbacks.computeIfPresent("callback", (key,value) -> value + key);
        callbacks.compute("COMPUTE", (key,value) -> value == null ? key : value);
        callbacks.merge("CALLBACK", "!", (left,right) -> left + right);
        out.add("callbacks=" + callbacks);
        Map<String,String> serialized = (Map<String,String>) type.newInstance(); serialized.put("Mixed", "value");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        new ObjectOutputStream(bytes).writeObject(serialized);
        ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray())) {
            @Override protected Class<?> resolveClass(ObjectStreamClass descriptor) throws IOException, ClassNotFoundException {
                try { return Class.forName(descriptor.getName(), false, l); }
                catch (ClassNotFoundException missing) { return super.resolveClass(descriptor); }
            }
        };
        Map<?,?> restored = (Map<?,?>) input.readObject();
        out.add("serialized=" + restored + ":" + restored.get("MIXED"));
        Locale old = Locale.getDefault();
        try {
            for (Locale locale : new Locale[]{Locale.US, new Locale("tr", "TR"), Locale.GERMANY}) {
                Locale.setDefault(locale); Map<String,String> localized = (Map<String,String>) type.newInstance(); localized.put("I", "value");
                out.add("locale-" + locale + "=" + localized + ":" + localized.get("i") + ":" + localized.get("I"));
            }
        } finally { Locale.setDefault(old); }
        return out;
    }
    public static void main(String[] args) throws Exception {
        try (URLClassLoader supplier=loader(new File(args[0]), new File(args[1])); URLClassLoader owned=loader(new File(args[2]), new File(args[3]))) {
            Set<String> expectedMethods = publicMethods(supplier.loadClass("com.sunwayworld.framework.data.CaseInsensitiveLinkedMap"));
            Set<String> actualMethods = publicMethods(owned.loadClass("com.sunwayworld.framework.data.CaseInsensitiveLinkedMap"));
            if (!expectedMethods.equals(actualMethods)) throw new AssertionError("public method contract supplier="+expectedMethods+" owned="+actualMethods);
            List<String> expected=observe(supplier), actual=observe(owned);
            if (!expected.equals(actual)) { for (int i=0;i<expected.size();i++) if (!expected.get(i).equals(actual.get(i))) throw new AssertionError("observation "+i+" supplier="+expected.get(i)+" owned="+actual.get(i)); }
            for (ClassLoader source : new ClassLoader[]{supplier, owned}) {
                Map<String,Object> original = (Map<String,Object>)source.loadClass("com.sunwayworld.framework.data.CaseInsensitiveLinkedMap").newInstance();
                original.put("Mixed", new ArrayList<String>(Arrays.asList("one","two"))); original.put(null, "null-value");
                ClassLoader destination = source == supplier ? owned : supplier;
                Map<?,?> restored = (Map<?,?>)deserialize(serialize(original), destination);
                if (restored.getClass().getClassLoader() != destination || !original.toString().equals(restored.toString()) || !original.get("mixed").equals(restored.get("MIXED"))) throw new AssertionError("cross-loader serialized map");
            }
            System.out.println("case-insensitive linked map differential: PASS ("+expected.size()+" observations, public methods, two-way serialization)");
        }
    }
}
