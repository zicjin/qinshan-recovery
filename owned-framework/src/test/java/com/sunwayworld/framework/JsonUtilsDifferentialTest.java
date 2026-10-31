package com.sunwayworld.framework;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/** Differential observations for JSON conversion and shape detection. */
public final class JsonUtilsDifferentialTest {
    public static final class Bean {
        private String name;
        private int count;
        public Bean() { }
        public String getName() { return name; }
        public void setName(String value) { name = value; }
        public int getCount() { return count; }
        public void setCount(int value) { count = value; }
        public String toString() { return name + ":" + count; }
    }
    private static URLClassLoader loader(File primary, File classes, File tests, File dependencies) throws Exception {
        List<URL> urls = new ArrayList<URL>(); urls.add(primary.toURI().toURL()); urls.add(classes.toURI().toURL()); urls.add(tests.toURI().toURL());
        for (File file : dependencies.listFiles()) if (file.getName().endsWith(".jar")) urls.add(file.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[0]), null);
    }
    private static Object call(Class<?> type, String name, Class<?>[] sig, Object... args) throws Exception { return type.getMethod(name, sig).invoke(null, args); }
    private static void equal(Object expected, Object actual, String label) {
        if (expected == null ? actual != null : !expected.equals(actual)) throw new AssertionError(label + " supplier=" + expected + " owned=" + actual);
    }
    public static void main(String[] args) throws Exception {
        try (URLClassLoader supplierLoader = loader(new File(args[0]), new File(args[2]), new File(args[3]), new File(args[1]));
             URLClassLoader ownedLoader = loader(new File(args[2]), new File(args[2]), new File(args[3]), new File(args[1]))) {
            Class<?> supplier = supplierLoader.loadClass("com.sunwayworld.framework.utils.JsonUtils");
            Class<?> owned = ownedLoader.loadClass("com.sunwayworld.framework.utils.JsonUtils");
            int count = 0;
            equal(call(supplier, "toJsonString", new Class<?>[]{Object.class}, "x"), call(owned, "toJsonString", new Class<?>[]{Object.class}, "x"), "string json"); count++;
            equal(call(supplier, "parse", new Class<?>[]{String.class, Class.class}, "42", Integer.class), call(owned, "parse", new Class<?>[]{String.class, Class.class}, "42", Integer.class), "simple parse"); count++;
            equal(call(supplier, "isArray", new Class<?>[]{String.class}, "[1,2]"), call(owned, "isArray", new Class<?>[]{String.class}, "[1,2]"), "array"); count++;
            equal(call(supplier, "isObject", new Class<?>[]{String.class}, "{\"x\":1}"), call(owned, "isObject", new Class<?>[]{String.class}, "{\"x\":1}"), "object"); count++;
            equal(call(supplier, "isArray", new Class<?>[]{String.class}, "bad"), call(owned, "isArray", new Class<?>[]{String.class}, "bad"), "invalid array"); count++;
            equal(call(supplier, "parseList", new Class<?>[]{String.class, Class.class}, "[\"a\",\"b\"]", String.class), call(owned, "parseList", new Class<?>[]{String.class, Class.class}, "[\"a\",\"b\"]", String.class), "list parse"); count++;
            Class<?> sBean = supplierLoader.loadClass("com.sunwayworld.framework.JsonUtilsDifferentialTest$Bean");
            Class<?> oBean = ownedLoader.loadClass("com.sunwayworld.framework.JsonUtilsDifferentialTest$Bean");
            Object sb = call(supplier, "parse", new Class<?>[]{String.class, Class.class}, "{\"name\":\"alpha\",\"count\":3}", sBean);
            Object ob = call(owned, "parse", new Class<?>[]{String.class, Class.class}, "{\"name\":\"alpha\",\"count\":3}", oBean);
            equal(sb.toString(), ob.toString(), "bean parse"); count++;
            System.out.println("json utils differential: PASS (" + count + " observations)");
        }
    }
}
