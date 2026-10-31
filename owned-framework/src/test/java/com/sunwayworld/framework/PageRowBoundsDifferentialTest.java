package com.sunwayworld.framework;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public final class PageRowBoundsDifferentialTest {
    private static List<String> observe(ClassLoader loader) throws Exception {
        List<String> out = new ArrayList<String>();
        Class<?> type = loader.loadClass("com.sunwayworld.framework.mybatis.page.PageRowBounds");
        Class<?> pageable = loader.loadClass("com.sunwayworld.framework.data.page.Pageable");
        for (long offset : new long[]{0,50,-5,(long)Integer.MAX_VALUE+1}) {
            for (int limit : new int[]{-1,0,50}) {
                final long position = offset; final int size = limit;
                String[] sums = {"amount"}, counts = {"id"};
                Object source = Proxy.newProxyInstance(loader,new Class[]{pageable},(proxy,method,args) -> {
                    switch(method.getName()) {
                        case "getOffset": return position;
                        case "getPageSize": return size;
                        case "getPageNumber": return 2;
                        case "getSumProperties": return sums;
                        case "getCountProperties": return counts;
                        case "getOptionsProperty": return "status";
                        default: throw new AssertionError("unexpected pageable method: "+method.getName());
                    }
                });
                try {
                    Object bounds = type.getConstructor(pageable).newInstance(source);
                    out.add("bounds="+offset+","+limit+":"+snapshot(type,bounds));
                    out.add("array-alias="+(sums==type.getMethod("getSumProperties").invoke(bounds))+":"+(counts==type.getMethod("getCountProperties").invoke(bounds)));
                    type.getMethod("setPageNumber",int.class).invoke(bounds,-1);
                    out.add("negative-page="+snapshot(type,bounds));
                } catch(InvocationTargetException e) { out.add("bounds="+offset+","+limit+"!"+e.getCause().getClass().getName()); }
            }
        }
        Object source = Proxy.newProxyInstance(loader,new Class[]{pageable},(proxy,method,args) -> {
            if(method.getName().equals("getOffset")) return 0L;
            if(method.getReturnType()==int.class) return 0;
            return null;
        });
        Object bounds = type.getConstructor(pageable).newInstance(source);
        out.add("defaults="+snapshot(type,bounds));
        Object emptyMap1=type.getMethod("getTotalMap").invoke(bounds), emptyMap2=type.getMethod("getTotalMap").invoke(bounds);
        out.add("default-total-map-alias="+(emptyMap1==emptyMap2));
        out.add("default-total-map-class="+emptyMap1.getClass().getName());
        for (String field : new String[]{"SumProperties","CountProperties"}) {
            String[] values={"x"}; type.getMethod("set"+field,String[].class).invoke(bounds,(Object)values);
            out.add(field+"-setter-alias="+(values==type.getMethod("get"+field).invoke(bounds)));
            type.getMethod("set"+field,String[].class).invoke(bounds,new Object[]{null});
            out.add(field+"-null="+Arrays.toString((String[])type.getMethod("get"+field).invoke(bounds)));
        }
        for(Object qty : new Object[]{1, -3, 2.9, Long.MAX_VALUE, new java.math.BigDecimal("7.4"), null}) {
            Map<String,Object> totals=new LinkedHashMap<String,Object>();totals.put("QtY_",qty);totals.put("AMOUNT",5);
            try {
                type.getMethod("setTotalMap",Map.class).invoke(bounds,totals);
                Map<?,?> actual=(Map<?,?>)type.getMethod("getTotalMap").invoke(bounds);
                out.add("totals="+qty+":"+snapshot(type,bounds)+":alias="+(actual==totals)+":lookup="+actual.get("QTY_"));
            } catch(InvocationTargetException e) { out.add("totals="+qty+"!"+e.getCause().getClass().getName()); }
        }
        type.getMethod("setTotal",int.class).invoke(bounds,17);
        type.getMethod("setTotalMap",Map.class).invoke(bounds,Collections.singletonMap("amount",5));
        out.add("total-without-qty="+snapshot(type,bounds));
        type.getMethod("setOptionsProperty",String.class).invoke(bounds,"category");
        out.add("options-setter="+snapshot(type,bounds));
        Class<?> pagination=loader.loadClass("com.sunwayworld.framework.data.page.Pagination");
        for(int total : new int[]{-1,0,17}) {
            type.getMethod("setTotal",int.class).invoke(bounds,total);
            List<String> rows=new ArrayList<String>(Arrays.asList("one","two"));
            Object result=pagination.getConstructor(type,List.class).newInstance(bounds,rows);
            out.add("pagination="+total+":"+pagination.getMethod("getTotal").invoke(result)+":"+
                pagination.getMethod("getPageSize").invoke(result)+":"+pagination.getMethod("getPageNumber").invoke(result)+":"+
                (pagination.getMethod("getRows").invoke(result)==rows)+":"+
                (pagination.getMethod("getTotalMap").invoke(result)==type.getMethod("getTotalMap").invoke(bounds)));
        }
        try {pagination.getConstructor(type,List.class).newInstance(bounds,null);out.add("pagination-null-rows=accepted");}
        catch(InvocationTargetException e) {out.add("pagination-null-rows="+e.getCause().getClass().getName());}
        try {pagination.getConstructor(type,List.class).newInstance(null,new ArrayList<String>());out.add("pagination-null-bounds=accepted");}
        catch(InvocationTargetException e) {out.add("pagination-null-bounds="+e.getCause().getClass().getName());}
        try { type.getMethod("setTotalMap",Map.class).invoke(bounds,new Object[]{null});out.add("null-map=accepted"); }
        catch(InvocationTargetException e) {out.add("null-map="+e.getCause().getClass().getName());}
        try {type.getConstructor(pageable).newInstance(new Object[]{null});out.add("null-pageable=accepted");}
        catch(InvocationTargetException e) {out.add("null-pageable="+e.getCause().getClass().getName());}
        return out;
    }
    private static String snapshot(Class<?> type,Object value)throws Exception {
        return type.getMethod("getOffset").invoke(value)+":"+type.getMethod("getLimit").invoke(value)+":"+
            type.getMethod("getPageNumber").invoke(value)+":"+type.getMethod("getTotal").invoke(value)+":"+
            type.getMethod("isPageEnabled").invoke(value)+":"+type.getMethod("getTotalMap").invoke(value)+":"+
            Arrays.toString((String[])type.getMethod("getSumProperties").invoke(value))+":"+
            Arrays.toString((String[])type.getMethod("getCountProperties").invoke(value))+":"+type.getMethod("getOptionsProperty").invoke(value);
    }
    private static URL[] urls(String directory,String classes)throws Exception {
        File[] files=new File(directory).listFiles();Arrays.sort(files);List<URL> result=new ArrayList<URL>();
        if(classes!=null)result.add(new File(classes).toURI().toURL());
        for(File file:files)if(file.getName().endsWith(".jar"))result.add(file.toURI().toURL());
        return result.toArray(new URL[0]);
    }
    public static void main(String[] args)throws Exception {
        try(URLClassLoader supplier=new URLClassLoader(urls(args[0],null),null)) {
            List<String> expected=observe(supplier);
            if(args.length==1) {for(String line:expected)System.out.println(line);return;}
            try(URLClassLoader owned=new URLClassLoader(urls(args[1],args[2]),null)) {
                List<String> actual=observe(owned);
                if(!expected.equals(actual)) {
                    for(int i=0;i<Math.min(expected.size(),actual.size());i++)if(!expected.get(i).equals(actual.get(i)))throw new AssertionError("supplier: "+expected.get(i)+"\nowned: "+actual.get(i));
                    throw new AssertionError("observation count differs");
                }
                System.out.println("page row bounds differential: PASS ("+expected.size()+" observations)");
            }
        }
    }
}
