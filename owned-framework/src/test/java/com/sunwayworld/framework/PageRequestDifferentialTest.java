package com.sunwayworld.framework;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/** Isolated supplier/owned observations through the public PageRequest API. */
public final class PageRequestDifferentialTest {
    private static URLClassLoader loader(File deps, File framework) throws Exception {
        List<URL> urls = new ArrayList<URL>();
        urls.add(framework.toURI().toURL());
        for (File file : deps.listFiles()) if (file.getName().endsWith(".jar")) urls.add(file.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[0]), null);
    }

    private static String state(Object request) throws Exception {
        Class<?> type = request.getClass();
        return type.getMethod("getPageNumber").invoke(request) + ":"
                + type.getMethod("getPageSize").invoke(request) + ":"
                + type.getMethod("getOffset").invoke(request) + ":"
                + type.getMethod("hasPrevious").invoke(request) + ":"
                + type.getMethod("getFilterJson").invoke(request) + ":"
                + type.getMethod("getSort").invoke(request) + ":"
                + Arrays.toString((String[]) type.getMethod("getSumProperties").invoke(request)) + ":"
                + Arrays.toString((String[]) type.getMethod("getCountProperties").invoke(request)) + ":"
                + type.getMethod("getOptionsProperty").invoke(request);
    }

    private static String call(Object value, Method method, Object... args) throws Exception {
        try { return String.valueOf(method.invoke(value, args)); }
        catch (InvocationTargetException error) { return "ERROR:" + error.getCause().getClass().getName(); }
    }

    private static List<String> observe(ClassLoader loader) throws Exception {
        Class<?> type = loader.loadClass("com.sunwayworld.framework.data.page.PageRequest");
        List<String> out = new ArrayList<String>();
        out.add("default=" + state(type.newInstance()));
        for (int[] pair : new int[][] {{1, 50}, {3, 25}, {0, 0}, {-1, 10}, {2, -1}, {-1, -1}, {Integer.MAX_VALUE, 50}}) {
            Object request = type.getMethod("of", int.class, int.class).invoke(null, pair[0], pair[1]);
            out.add("factory=" + state(request));
            type.getMethod("setFilterJson", String.class).invoke(request, "{\"status\":\"OPEN\"}");
            String[] sums = {"amount", "total"}, counts = {"id"};
            type.getMethod("setSumProperties", String[].class).invoke(request, (Object) sums);
            type.getMethod("setCountProperties", String[].class).invoke(request, (Object) counts);
            type.getMethod("setOptionsProperty", String.class).invoke(request, "status");
            Class<?> sort = loader.loadClass("org.springframework.data.domain.Sort");
            type.getMethod("setSort", sort).invoke(request, sort.getMethod("by", String[].class).invoke(null, (Object) new String[] {"name"}));
            out.add("metadata=" + state(request));
            out.add("array-alias=" + (sums == type.getMethod("getSumProperties").invoke(request)) + ":"
                    + (counts == type.getMethod("getCountProperties").invoke(request)));
            for (String navigation : new String[] {"next", "previousOrFirst", "first"}) {
                Object next = type.getMethod(navigation).invoke(request);
                out.add(navigation + "=" + state(next) + ":same=" + (next == request));
            }
            out.add("map=" + call(request, type.getMethod("map", Class.class), HashMap.class));
            for (String json : new String[] {null, "", " ", "null", "{"}) {
                type.getMethod("setFilterJson", String.class).invoke(request, json);
                out.add("map:" + json + "=" + call(request, type.getMethod("map", Class.class), HashMap.class));
            }
        }
        Class<?> wrapperType = loader.loadClass("com.sunwayworld.framework.restful.data.RestJsonWrapperBean");
        for (String[] pair : new String[][] {{null, null}, {"", ""}, {"4", "12"}, {"0x10", "1,000"}, {"-1", "-1"}, {"abc", "12"}}) {
            Object wrapper = wrapperType.getConstructor(String.class).newInstance((String) null);
            Method set = wrapperType.getMethod("setParamValue", String.class, String.class);
            set.invoke(wrapper, "n", pair[0]); set.invoke(wrapper, "s", pair[1]);
            set.invoke(wrapper, "f", "{\"status\":\"READY\"}");
            set.invoke(wrapper, "tf", "amount,total,"); set.invoke(wrapper, "cp", "id"); set.invoke(wrapper, "op", "status");
            try { out.add("wrapper=" + state(type.getMethod("of", wrapperType).invoke(null, wrapper))); }
            catch (InvocationTargetException error) { out.add("wrapper=ERROR:" + error.getCause().getClass().getName()); }
        }
        return out;
    }

    public static void main(String[] args) throws Exception {
        try (URLClassLoader supplier = loader(new File(args[0]), new File(args[1]));
             URLClassLoader owned = loader(new File(args[0]), new File(args[2]))) {
            List<String> expected = observe(supplier), actual = observe(owned);
            if (expected.size() != actual.size()) throw new AssertionError("observation count");
            for (int index = 0; index < expected.size(); index++) {
                if (!expected.get(index).equals(actual.get(index))) {
                    throw new AssertionError("at " + index + " supplier=" + expected.get(index) + " owned=" + actual.get(index));
                }
            }
            System.out.println("PageRequest differential: PASS (" + expected.size() + " observations)");
        }
    }
}
