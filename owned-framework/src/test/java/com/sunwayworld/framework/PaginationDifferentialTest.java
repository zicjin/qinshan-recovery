package com.sunwayworld.framework;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/** Observes only Pagination's public Java contract through isolated loaders. */
public final class PaginationDifferentialTest {
    private static Class<?> type;
    private static final List<String> observations = new ArrayList<String>();
    private static Object make(Class<?>[] types, Object... args) throws Exception {
        return type.getConstructor(types).newInstance(args);
    }
    private static void record(String label, Object p) throws Exception {
        StringBuilder text = new StringBuilder(label);
        for (String name : new String[]{"getTotal", "getPageSize", "getPageNumber", "getTotalPages", "getNumberOfElements", "isFirst", "isLast", "hasNext", "hasPrevious", "getRows", "getColumnTypeMap", "getOptionsList", "getTotalMap"}) {
            text.append('|').append(name).append('=');
            try { text.append(type.getMethod(name).invoke(p)); }
            catch (InvocationTargetException e) { text.append('!').append(e.getCause().getClass().getName()); }
        }
        observations.add(text.toString());
    }
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static List<String> observe(ClassLoader loader) throws Exception {
        observations.clear(); type = loader.loadClass("com.sunwayworld.framework.data.page.Pagination");
        record("default", make(new Class[]{}));
        Object defaults = make(new Class[]{});
        for (String getter : new String[]{"getRows", "getTotalMap", "getColumnTypeMap"}) {
            Object collection = type.getMethod(getter).invoke(defaults);
            try {
                if (collection instanceof List) ((List) collection).add("added");
                else ((Map) collection).put("added", 1);
                observations.add("default-" + getter + "-mutable=true");
            } catch (UnsupportedOperationException e) {
                observations.add("default-" + getter + "-mutable=false");
            }
        }
        for (List rows : new List[]{new ArrayList(), new ArrayList(Arrays.asList("a", "b")), null}) {
            try {
                Object p = make(new Class[]{List.class}, rows); record("list=" + rows, p);
                List result = (List)type.getMethod("getRows").invoke(p);
                observations.add("list-alias=" + (result == rows)); result.add("append"); record("mutated-list", p);
            } catch (InvocationTargetException e) { observations.add("list=" + rows + "!" + e.getCause().getClass().getName()); }
        }
        for (int[] v : new int[][]{{17,3,2}, {0,0,0}, {-2,-3,-11}, {1,0,10}, {1,-2,10}, {-3,0,1}, {-3,2,1}, {Integer.MAX_VALUE,2,1}}) {
            record(Arrays.toString(v), make(new Class[]{int.class,int.class,int.class,List.class}, v[0],v[1],v[2],new ArrayList(Arrays.asList("r"))));
        }
        try { make(new Class[]{int.class,int.class,int.class,List.class},1,2,3,null); observations.add("null-four=accepted"); }
        catch (InvocationTargetException e) { observations.add("null-four="+e.getCause().getClass().getName()); }
        Object p = make(new Class[]{});
        for (String setter : new String[]{"setTotal", "setPageSize", "setPageNumber"}) for (int n : new int[]{-3,0,1,9}) {
            type.getMethod(setter,int.class).invoke(p,n); record(setter+"="+n,p);
        }
        for (String prop : new String[]{"Rows","ColumnTypeMap","OptionsList","TotalMap"}) {
            Class arg = prop.equals("Rows") || prop.equals("OptionsList") ? List.class : Map.class;
            Object value = arg == List.class ? new ArrayList(Arrays.asList("v")) : new HashMap(Collections.singletonMap("k","v"));
            type.getMethod("set"+prop,arg).invoke(p,value);
            Object result = type.getMethod("get"+prop).invoke(p);
            observations.add(prop+"-alias="+(value == result));
            if (arg == List.class) ((List)result).add("extra"); else ((Map)result).put("z","q");
            record("mutated-"+prop,p);
            type.getMethod("set"+prop,arg).invoke(p,new Object[]{null}); record("null-"+prop,p);
        }
        Object source = make(new Class[]{int.class,int.class,int.class,List.class},17,3,2,new ArrayList(Arrays.asList("old")));
        type.getMethod("setTotalMap",Map.class).invoke(source,new HashMap(Collections.singletonMap("amount",5)));
        type.getMethod("setColumnTypeMap",Map.class).invoke(source,new HashMap(Collections.singletonMap("a","str")));
        type.getMethod("setOptionsList",List.class).invoke(source,new ArrayList(Arrays.asList("option")));
        Class<?> page = loader.loadClass("com.sunwayworld.framework.data.page.Page");
        List rows = new ArrayList(Arrays.asList("new")); Object copy = make(new Class[]{page,List.class},source,rows); record("copy",copy);
        for (String getter : new String[]{"getColumnTypeMap","getOptionsList","getTotalMap"}) observations.add("copy-"+getter+"-alias="+(type.getMethod(getter).invoke(source)==type.getMethod(getter).invoke(copy)));
        try { make(new Class[]{page,List.class},null,rows); observations.add("null-copy=accepted"); }
        catch (InvocationTargetException e) { observations.add("null-copy="+e.getCause().getClass().getName()); }
        type.getMethod("setTotal", int.class).invoke(source, -1);
        record("copy-more-rows-than-total", make(new Class[]{page,List.class},source,rows));
        try { make(new Class[]{page,List.class},source,null); observations.add("null-copy-rows=accepted"); }
        catch (InvocationTargetException e) { observations.add("null-copy-rows="+e.getCause().getClass().getName()); }
        for (Map totals : new Map[]{new HashMap(Collections.singletonMap("sum", 7)), null}) {
            try {
                Object withTotals = make(new Class[]{int.class,int.class,int.class,List.class,Map.class},17,3,2,rows,totals);
                record("five-args", withTotals);
                observations.add("five-args-total-alias="+(totals == type.getMethod("getTotalMap").invoke(withTotals)));
            } catch (InvocationTargetException e) { observations.add("five-args!"+e.getCause().getClass().getName()); }
        }
        Class<?> springPage = loader.loadClass("org.springframework.data.domain.Page");
        for (long total : new long[]{0L, 17L, (long)Integer.MAX_VALUE+2}) {
            final List springRows = new ArrayList(Arrays.asList("spring"));
            final long springTotal = total;
            Object springSource = Proxy.newProxyInstance(loader, new Class[]{springPage}, (proxy, method, args) -> {
                if (method.getName().equals("getTotalElements")) return springTotal;
                if (method.getName().equals("getContent")) return springRows;
                throw new AssertionError("unexpected Spring Page method: " + method.getName());
            });
            Object springCopy = make(new Class[]{int.class,int.class,springPage},3,2,springSource);
            record("spring="+total,springCopy);
            observations.add("spring-rows-alias="+(springRows == type.getMethod("getRows").invoke(springCopy)));
        }
        try { make(new Class[]{int.class,int.class,springPage},3,2,null); observations.add("null-spring=accepted"); }
        catch (InvocationTargetException e) { observations.add("null-spring="+e.getCause().getClass().getName()); }
        return new ArrayList<String>(observations);
    }
    public static void main(String[] args) throws Exception {
        File[] jars = new File(args[0]).listFiles(); Arrays.sort(jars); URL[] urls = new URL[jars.length];
        for (int i=0;i<jars.length;i++) urls[i]=jars[i].toURI().toURL();
        // Use public libraries in the owned loader, never the supplier framework.
        List<URL> publicUrls = new ArrayList<URL>();
        publicUrls.add(new File(args[1]).toURI().toURL());
        File[] publicJars = new File(args[2]).listFiles(); Arrays.sort(publicJars);
        for (File jar : publicJars) publicUrls.add(jar.toURI().toURL());
        URLClassLoader supplier = new URLClassLoader(urls,null), owned = new URLClassLoader(publicUrls.toArray(new URL[0]),null);
        List<String> expected=observe(supplier), actual=observe(owned);
        if (expected.size() != actual.size()) throw new AssertionError("observation count differs");
        for(int i=0;i<expected.size();i++) if(!expected.get(i).equals(actual.get(i))) throw new AssertionError("supplier: "+expected.get(i)+"\nowned: "+actual.get(i));
        for(String line:expected) System.out.println(line);
        System.out.println("pagination differential: PASS ("+expected.size()+" observations)"); supplier.close();owned.close();
    }
}
