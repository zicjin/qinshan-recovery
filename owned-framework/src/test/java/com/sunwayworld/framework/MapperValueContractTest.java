package com.sunwayworld.framework;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Differential value-object seam for mapper parameter models. */
public final class MapperValueContractTest {
    public static class StringLengthConverter implements com.sunwayworld.framework.converter.Converter<String, Number> {
        public Number convert(String value) { return value.length(); }
    }
    private static URLClassLoader loader(File directory, File owned, File fixture) throws Exception {
        List<URL> urls = new ArrayList<URL>();
        if (owned != null) urls.add(owned.toURI().toURL());
        if (fixture != null) urls.add(fixture.toURI().toURL());
        File[] jars = directory.listFiles(); Arrays.sort(jars);
        for (File jar : jars) urls.add(jar.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), MapperValueContractTest.class.getClassLoader().getParent());
    }
    private static String invoke(Method method, Object receiver, Object... args) throws Exception {
        try { return String.valueOf(method.invoke(receiver, args)); }
        catch (InvocationTargetException error) { return "error:" + error.getCause().getClass().getName(); }
    }
    private static List<String> observe(ClassLoader loader) throws Exception {
        List<String> out = new ArrayList<String>();
        Class<?> advanced = loader.loadClass("com.sunwayworld.framework.mybatis.mapper.AdvancedFilterPattern");
        Object a = advanced.getConstructor(String.class, Object.class, String.class).newInstance("NAME", "Alice", "EQ");
        for (String getter : new String[]{"isGroupStart", "isGroupEnd", "getGroupLink", "getIngroupLink", "getName", "getValue", "getMatchPattern"}) out.add("a0:" + getter + "=" + invoke(advanced.getMethod(getter), a));
        advanced.getMethod("setGroupStart", boolean.class).invoke(a, true); advanced.getMethod("setGroupEnd", boolean.class).invoke(a, true);
        advanced.getMethod("setGroupLink", String.class).invoke(a, "OR"); advanced.getMethod("setIngroupLink", String.class).invoke(a, "AND");
        advanced.getMethod("setName", String.class).invoke(a, "CODE"); advanced.getMethod("setValue", Object.class).invoke(a, 7); advanced.getMethod("setMatchPattern", String.class).invoke(a, "NEQ");
        for (String getter : new String[]{"isGroupStart", "isGroupEnd", "getGroupLink", "getIngroupLink", "getName", "getValue", "getMatchPattern"}) out.add("a1:" + getter + "=" + invoke(advanced.getMethod(getter), a));
        Object nullAdvanced = advanced.getConstructor(String.class, Object.class, String.class).newInstance(null, null, null);
        out.add("anull=" + invoke(advanced.getMethod("getName"), nullAdvanced) + ":" + invoke(advanced.getMethod("getValue"), nullAdvanced));
        Class<?> multi = loader.loadClass("com.sunwayworld.framework.mybatis.mapper.MultiPropertyFilterPattern");
        String[] names = new String[]{"name", "code"};
        for (Object sensitive : new Object[]{null, Boolean.TRUE, Boolean.FALSE}) {
            Object m = multi.getConstructor(String[].class, String.class, Boolean.class).newInstance(names, "AbC", sensitive);
            out.add("m:" + sensitive + ":" + Arrays.toString((Object[]) multi.getMethod("getNames").invoke(m)) + ":" + multi.getMethod("getValue").invoke(m) + ":" + multi.getMethod("getCaseSensitive").invoke(m));
            ((String[]) multi.getMethod("getNames").invoke(m))[0] = "changed";
            out.add("malias=" + ((String[]) multi.getMethod("getNames").invoke(m))[0]);
        }
        try { multi.getConstructor(String[].class, String.class, Boolean.class).newInstance(new String[]{"x"}, null, Boolean.FALSE); out.add("mnull=accepted"); }
        catch (InvocationTargetException error) { out.add("mnull=" + error.getCause().getClass().getName()); }
        Class<?> order = loader.loadClass("com.sunwayworld.framework.database.sql.Order");
        for (String direction : new String[]{"asc", "desc"}) { Object value = order.getMethod(direction, String.class).invoke(null, "CREATEDTIME"); out.add("order:" + direction + ":" + order.getMethod("getColumn").invoke(value) + ":" + order.getMethod("getDirection").invoke(value)); }
        for (String direction : new String[]{"asc", "desc"}) {
            try { order.getMethod(direction, String.class).invoke(null, new Object[]{null}); out.add("order-null:" + direction + "=accepted"); }
            catch (InvocationTargetException error) { out.add("order-null:" + direction + "=" + error.getCause().getClass().getName()); }
        }
        Class<?> pair = loader.loadClass("com.sunwayworld.framework.data.Pair");
        Object p = pair.getMethod("of", Object.class, Object.class).invoke(null, "NAME", " ASC");
        out.add("pair=" + pair.getMethod("getFirst").invoke(p) + ":" + pair.getMethod("getSecond").invoke(p));
        pair.getMethod("setFirst", Object.class).invoke(p, "CODE");
        pair.getMethod("setSecond", Object.class).invoke(p, " DESC");
        out.add("pair-mutated=" + pair.getMethod("getFirst").invoke(p) + ":" + pair.getMethod("getSecond").invoke(p));
        Object same = pair.getMethod("of", Object.class, Object.class).invoke(null, "CODE", " DESC");
        out.add("pair-equals=" + p.equals(same));
        Object nullPair = pair.getMethod("of", Object.class, Object.class).invoke(null, null, null);
        out.add("pair-null=" + pair.getMethod("getFirst").invoke(nullPair) + ":" + pair.getMethod("getSecond").invoke(nullPair));
        Class<?> typed = loader.loadClass("com.sunwayworld.framework.data.TypeValuePair");
        Object t = typed.getConstructor(Class.class, Object.class).newInstance(String.class, "value");
        out.add("typed=" + typed.getMethod("getFirst").invoke(t) + ":" + typed.getMethod("getSecond").invoke(t));
        Class<?> converter = loader.loadClass("com.sunwayworld.framework.MapperValueContractTest$StringLengthConverter");
        Object c = converter.newInstance();
        out.add("converter-source=" + converter.getMethod("getSourceType").invoke(c));
        out.add("converter-target=" + converter.getMethod("getTargetType").invoke(c));
        for (Class<?> source : new Class<?>[]{String.class, Object.class}) {
            for (Class<?> target : new Class<?>[]{Integer.class, Number.class, String.class}) {
                out.add("converter:" + source + ":" + target + "=" + converter.getMethod("canConvert", Class.class, Class.class).invoke(c, source, target));
            }
        }
        return out;
    }
    public static void main(String[] args) throws Exception {
        try (URLClassLoader reference = loader(new File(args[0]), null, new File(args[3])); URLClassLoader owned = loader(new File(args[1]), new File(args[2]), new File(args[3]))) {
            List<String> expected = observe(reference), actual = observe(owned);
            if (!expected.equals(actual)) throw new AssertionError("mapper value observations differ: " + expected + " / " + actual);
            System.out.println("mapper value differential: PASS (" + actual.size() + " observations)");
        }
    }
}
