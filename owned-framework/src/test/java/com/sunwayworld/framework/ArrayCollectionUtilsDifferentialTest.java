package com.sunwayworld.framework;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Public utility seam differential: arrays and collections use isolated supplier/owned loaders. */
public final class ArrayCollectionUtilsDifferentialTest {
    private interface Action { Object run() throws Exception; }
    private static URLClassLoader loader(File implementation, File dependencies) throws Exception {
        List<URL> urls = new ArrayList<URL>(); urls.add(implementation.toURI().toURL());
        for (File jar : dependencies.listFiles()) if (jar.getName().endsWith(".jar")) urls.add(jar.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[0]), null);
    }
    private static String describe(Object value) {
        if (value == null) return "null";
        if (value.getClass().isArray()) {
            int length = java.lang.reflect.Array.getLength(value); List<String> values = new ArrayList<String>();
            for (int i=0;i<length;i++) values.add(describe(java.lang.reflect.Array.get(value, i)));
            return value.getClass().getName() + values;
        }
        if (value instanceof Collection<?>) return value.getClass().getName() + ":" + value;
        if (value instanceof Map<?,?>) return value.getClass().getName() + ":" + value;
        return value.getClass().getName() + ":" + value;
    }
    private static String failure(Throwable error) {
        return error.getClass().getName() + ":" + String.valueOf(error.getMessage())
                + (error.getCause() == null ? "" : " <- " + failure(error.getCause()));
    }
    private static List<String> observe(ClassLoader loader) throws Exception {
        Class<?> arrays = loader.loadClass("com.sunwayworld.framework.utils.ArrayUtils");
        Class<?> collections = loader.loadClass("com.sunwayworld.framework.utils.CollectionUtils");
        List<String> out = new ArrayList<String>();
        record(out, "array-type", () -> arrays.getMethod("getArrayType", Class.class).invoke(null, String[][].class));
        record(out, "array-type-error", () -> arrays.getMethod("getArrayType", Class.class).invoke(null, String.class));
        record(out, "has-element", () -> arrays.getMethod("hasElement", Object.class).invoke(null, new int[] {1}));
        record(out, "has-empty", () -> arrays.getMethod("hasElement", Object.class).invoke(null, new int[0]));
        record(out, "contains", () -> arrays.getMethod("contains", Object[].class, Object.class).invoke(null, new Object[] {new String[] {"x", null}}, "x"));
        record(out, "contains-ignore", () -> arrays.getMethod("containsIgnoreCase", String[].class, String.class).invoke(null, new String[] {"Alpha", null}, "alpha"));
        record(out, "concat", () -> arrays.getMethod("concat", Object[].class, Object[].class).invoke(null, new Object[] {new String[] {"a"}, new String[] {"b"}}));
        record(out, "append", () -> arrays.getMethod("append", Object[].class, Object.class).invoke(null, new Object[] {new String[] {"a"}, "b"}));
        record(out, "delimited", () -> arrays.getMethod("arrayToDelimitedString", Object[].class, String.class).invoke(null, new Object[] {new Integer[] {1, 2, null}, ","}));
        record(out, "empty-array", () -> arrays.getMethod("emptyArray", Class.class).invoke(null, String.class));
        record(out, "instance-array", () -> arrays.getMethod("instanceToArray", Object.class).invoke(null, "x"));
        record(out, "unique", () -> arrays.getMethod("getUniqueTypeValue", Object[].class, Class.class).invoke(null, new Object[] {new Object[] {"x", 1}, String.class}));
        record(out, "assignable", () -> arrays.getMethod("getUniqueAssignableType", Class[].class, Class.class).invoke(null, new Object[] {new Class<?>[] {String.class, Integer.class}, Number.class}));
        record(out, "equals", () -> arrays.getMethod("isEquals", Object[].class, Object[].class).invoke(null, new Object[] {new String[] {"x", null}, new String[] {"x", null}}));
        record(out, "as-list", () -> arrays.getMethod("asList", Object[].class).invoke(null, new Object[] {new String[] {"x", "y"}}));
        record(out, "subarray", () -> arrays.getMethod("subarray", Object[].class, int.class, int.class).invoke(null, new Object[] {new String[] {"a", "b", "c"}, 1, 2}));
        record(out, "int-subarray", () -> arrays.getMethod("subarray", int[].class, int.class, int.class).invoke(null, new int[] {1, 2, 3}, 1, 2));
        record(out, "long-subarray", () -> arrays.getMethod("subarray", long[].class, int.class, int.class).invoke(null, new long[] {1, 2, 3}, 1, 2));
        record(out, "double-subarray", () -> arrays.getMethod("subarray", double[].class, int.class, int.class).invoke(null, new double[] {1, 2, 3}, 1, 2));
        record(out, "byte-subarray", () -> arrays.getMethod("subarray", byte[].class, int.class, int.class).invoke(null, new byte[] {1, 2, 3}, 1, 2));
        record(out, "int-concat", () -> arrays.getMethod("concat", int[].class, int[].class).invoke(null, new int[] {1}, new int[] {2}));
        record(out, "long-concat", () -> arrays.getMethod("concat", long[].class, long[].class).invoke(null, new long[] {1}, new long[] {2}));
        record(out, "double-concat", () -> arrays.getMethod("concat", double[].class, double[].class).invoke(null, new double[] {1}, new double[] {2}));
        record(out, "byte-concat", () -> arrays.getMethod("concat", byte[].class, byte[].class).invoke(null, new byte[] {1}, new byte[] {2}));
        record(out, "require", () -> arrays.getMethod("requireNotEmpty", Object[].class).invoke(null, new Object[] {new String[] {"x"}}));
        record(out, "require-error", () -> arrays.getMethod("requireNotEmpty", Object[].class).invoke(null, new Object[] {new String[0]}));
        record(out, "array-empty", () -> arrays.getMethod("isEmpty", Object[].class).invoke(null, new Object[] {new String[0]}));
        List<Integer> left = new ArrayList<Integer>(Arrays.asList(1, 2, 2)); List<Integer> right = new ArrayList<Integer>(Arrays.asList(2, 3));
        record(out, "union", () -> collections.getMethod("union", Collection.class, Collection.class).invoke(null, left, right));
        record(out, "collection-empty", () -> collections.getMethod("isEmpty", Collection.class).invoke(null, new ArrayList<Integer>()));
        record(out, "map-empty", () -> collections.getMethod("isEmpty", Map.class).invoke(null, new LinkedHashMap<String,Object>()));
        record(out, "intersection", () -> collections.getMethod("intersection", Collection.class, Collection.class).invoke(null, left, right));
        record(out, "disjunction", () -> collections.getMethod("disjunction", Collection.class, Collection.class).invoke(null, left, right));
        record(out, "subtract", () -> collections.getMethod("subtract", Collection.class, Collection.class).invoke(null, left, right));
        record(out, "merge", () -> { List<Integer> x = new ArrayList<Integer>(Arrays.asList(1, 2)); collections.getMethod("merge", Collection.class, Collection.class).invoke(null, x, Arrays.asList(2, 3)); return x; });
        record(out, "contains-ignore", () -> collections.getMethod("containsIgnoreCase", Collection.class, String.class).invoke(null, Arrays.asList("Alpha", "Beta"), "alpha"));
        Map<String,Object> map = new LinkedHashMap<String,Object>(); map.put("Name", "Alice"); map.put("empty", "");
        record(out, "map-first", () -> collections.getMethod("getFirstValue", Map.class).invoke(null, map));
        record(out, "map-contains", () -> collections.getMethod("containsKeyIgnorecase", Map.class, String.class).invoke(null, map, "name"));
        record(out, "map-contains-ignore", () -> collections.getMethod("containsIgnoreCase", Map.class, String.class).invoke(null, map, "name"));
        record(out, "map-value", () -> collections.getMethod("getValueIgnorecase", Map.class, String.class).invoke(null, map, "NAME"));
        record(out, "collection-first", () -> collections.getMethod("getFirstValue", Collection.class).invoke(null, Arrays.asList("first", "second")));
        record(out, "convert", () -> collections.getMethod("convert", List.class, Class.class).invoke(null, Arrays.asList("1", "2"), Integer.class));
        record(out, "distinct", () -> collections.getMethod("distinct", List.class).invoke(null, left));
        record(out, "lower", () -> { Map<String,Object> x = new LinkedHashMap<String,Object>(); x.put("Name", 1); collections.getMethod("lowerCaseKey", Map.class).invoke(null, x); return x; });
        record(out, "upper", () -> { Map<String,Object> x = new LinkedHashMap<String,Object>(); x.put("name", 1); collections.getMethod("upperCaseKey", Map.class).invoke(null, x); return x; });
        record(out, "put", () -> { Map<String,Object> x = new LinkedHashMap<String,Object>(); collections.getMethod("putIfNotBlank", Map.class, Object.class, Object.class).invoke(null, x, "a", " \t"); collections.getMethod("putIfNotBlank", Map.class, Object.class, Object.class).invoke(null, x, "nbsp", "\u00a0"); collections.getMethod("putIfNotBlank", Map.class, Object.class, Object.class).invoke(null, x, "b", 2); return x; });
        record(out, "sort", () -> { List<Integer> x = new ArrayList<Integer>(Arrays.asList(3, 1, 2)); Comparator<Integer> c = (a,b) -> a.compareTo(b); collections.getMethod("sort", List.class, Comparator.class).invoke(null, x, c); return x; });
        record(out, "empty-list", () -> collections.getMethod("emptyList").invoke(null));
        record(out, "empty-map", () -> collections.getMethod("emptyMap").invoke(null));
        return out;
    }
    private static void record(List<String> out, String name, Action action) {
        try { out.add(name + "=" + describe(action.run())); }
        catch (InvocationTargetException error) { out.add(name + "=!" + failure(error.getCause())); }
        catch (Throwable error) { out.add(name + "=!" + failure(error)); }
    }
    public static void main(String[] args) throws Exception {
        try (URLClassLoader supplier = loader(new File(args[0]), new File(args[3])); URLClassLoader owned = loader(new File(args[1]), new File(args[3]))) {
            List<String> expected = observe(supplier), actual = observe(owned);
            if (expected.toString().contains("NoClassDefFoundError") || actual.toString().contains("NoClassDefFoundError")
                    || expected.toString().contains("ClassNotFoundException") || actual.toString().contains("ClassNotFoundException"))
                throw new AssertionError("utility probe did not load its declared dependencies");
            if (!expected.equals(actual)) throw new AssertionError("supplier=" + expected + "\nowned=" + actual);
            System.out.println("Array/collection utility differential: PASS (" + expected.size() + " observations)");
        }
    }
}
